package store.aurora.config.security.handler.loginHandler.success;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.config.security.exception.connectionFail.UserClientConnectionFailException;
import store.aurora.feignClient.AuthClient;
import store.aurora.feignClient.UserClient;
import store.aurora.user.dto.request.SignUpRequest;
import store.aurora.user.exception.UserSignUpFailException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserClient userClient;
    private final AuthClient authClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //Oauth2로 로그인한 순간에는 각 idNo가 username임. 이후 다음 요청 부터는 payco:{idNo}형태가 됨. 혹시 로그인 한 순간에 뭔가를 가져올 일이 있다면 주의해야함.
        log.info("authentication username={}", authentication.getName());

        //1. 회원이 있는지 검색
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getName();
        String registrationId = oAuth2User.getAttribute("registrationId");
        String id = String.format("%s:%s", registrationId, username);

        Boolean userExists;

        try{
            userExists = userClient.checkUserExists(id);
        }catch (Exception e){
            log.info("user client 통신 실패");
            response.sendRedirect("/login");
            return;
        }


        //2. 회원 가입
        if(!userExists){
            Optional<SignUpRequest> optionalDto = makeSignUpRequest(id, oAuth2User, registrationId);
            if(optionalDto.isEmpty()){
                throw new UserSignUpFailException("missing attributes:" + oAuth2User.getAttributes());
            }
            Map<String, String> result = userClient.signUp(optionalDto.get(), true);

            if(Objects.nonNull(result) && result.containsKey("message")){  //todo 회원가입 실패 고려 http status code를 보고 판단할 예정
                log.info("회원가입 결과:{}", result.get("message"));
            }
        }


        //3. jwt토큰 만들기
        Optional<Cookie> optionalCookie = jwtOven(id);
        if(optionalCookie.isEmpty()){
            log.info("token make fail");
        }
        else {
            response.addCookie(optionalCookie.get());
        }

        //4. 필요없어진 액세스토큰과 리프레시 토큰을 만료시킨다?? todo 토론 필요 + 토큰 유효시간 감소 필요성

        //todo 로그인 되고 보낼 곳 정하기
        response.sendRedirect("/");
    }

    private Optional<SignUpRequest> makeSignUpRequest(String id, OAuth2User user, String registrationId){
        SignUpRequest signUpRequest;

        //payco의 경우
        if(registrationId.equals("payco")){

            String name = user.getAttribute("name");
            String birthdayMMdd = user.getAttribute("birthdayMMdd");
            String mobile = user.getAttribute("mobile");
            String phone = user.getAttribute("email");

            if(isNullOrBlank(name) || isNullOrBlank(birthdayMMdd) || isNullOrBlank(mobile) || isNullOrBlank(phone)){
                return Optional.empty();
            }

            signUpRequest = new SignUpRequest(
                    id,
                    null,
                    name,
                    String.format("1900%s", birthdayMMdd),
                    mobile,
                    phone,
                    null
            );
            return Optional.of(signUpRequest);
        }

        return Optional.empty();
    }

    private Optional<Cookie> jwtOven(String id){
        try{
            String jwtToken = authClient.getJwtToken(new JwtRequestDto(id));
            if(isNullOrBlank(jwtToken)){
                return Optional.empty();
            }
            Cookie cookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwtToken);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);

            return Optional.of(cookie);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    private boolean isNullOrBlank(String value){
        return Objects.isNull(value) || value.isBlank();
    }
}
