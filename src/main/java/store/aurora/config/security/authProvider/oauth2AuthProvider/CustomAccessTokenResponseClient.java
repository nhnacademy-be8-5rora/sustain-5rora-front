package store.aurora.config.security.authProvider.oauth2AuthProvider;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.response.PaycoAccessTokenResponse;
import store.aurora.feign_client.payco.PaycoTokenClient;


//액세스토큰 발급하는 놈
@Component
@RequiredArgsConstructor
public class CustomAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final DefaultAuthorizationCodeTokenResponseClient delegate;

    private final PaycoTokenClient paycoTokenClient;

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest) {

        //payco의 경우
        if(grantRequest.getClientRegistration().getRegistrationId().equals("payco")){
            return makePaycoToken(grantRequest);
        }

        //나머지는 디폴트로
        return delegate.getTokenResponse(grantRequest);
    }

    private OAuth2AccessTokenResponse makePaycoToken(OAuth2AuthorizationCodeGrantRequest grantRequest){
        PaycoAccessTokenResponse tokenResponse = paycoTokenClient.getAccessTokenByAuthorizationCode(
                "authorization_code",
                grantRequest.getClientRegistration().getClientId(),
                grantRequest.getClientRegistration().getClientSecret(),
                grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());

        return OAuth2AccessTokenResponse
                .withToken(tokenResponse.accessToken())
                .refreshToken(tokenResponse.refreshToken())
                .expiresIn(Long.parseLong(tokenResponse.expiresIn()))
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .build();
    }
}
