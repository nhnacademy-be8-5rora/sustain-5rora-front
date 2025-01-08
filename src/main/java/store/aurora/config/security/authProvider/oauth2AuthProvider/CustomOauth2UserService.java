package store.aurora.config.security.authProvider.oauth2AuthProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.PaycoInfoMember;
import store.aurora.auth.dto.request.PaycoInfoResponse;
import store.aurora.config.security.exception.userInfoNotFound.PaycoUserInfoNotFound;
import store.aurora.feign_client.payco.PaycoInfoClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate;

    private final PaycoInfoClient paycoInfoClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //payco 회원일 경우
        if(userRequest.getClientRegistration().getRegistrationId().equals("payco")){
            return makePaycoUser(userRequest);
        }

        //그 외에는 default사용
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        HashMap<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("registrationId", userRequest.getClientRegistration().getRegistrationId());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private OAuth2User makePaycoUser(OAuth2UserRequest userRequest){
        PaycoInfoResponse paycoUserInfo = paycoInfoClient.getInfoByAccessToken(userRequest.getClientRegistration().getClientId(),
                userRequest.getAccessToken().getTokenValue());

        if(!paycoUserInfo.header().isSuccessful()){
            throw new PaycoUserInfoNotFound(paycoUserInfo.header().resultCode(), paycoUserInfo.header().resultMessage());
        }

        Map<String, Object> attributes = extractAttributes(userRequest, paycoUserInfo);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")), //일단 payco로그인은 다 일반유저
                attributes,
                "idNo"
        );
    }

    private static Map<String, Object> extractAttributes(OAuth2UserRequest userRequest, PaycoInfoResponse paycoUserInfo) {
        PaycoInfoMember member = paycoUserInfo.data().member();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("idNo", member.idNo());
        attributes.put("email", member.email());
        attributes.put("mobile", member.mobile());
        attributes.put("maskedEmail", member.maskedEmail());
        attributes.put("maskedMobile", member.maskedMobile());
        attributes.put("name", member.name());
        attributes.put("birthdayMMdd", member.birthdayMMdd());
        attributes.put("registrationId", userRequest.getClientRegistration().getRegistrationId());
        return attributes;
    }
}
