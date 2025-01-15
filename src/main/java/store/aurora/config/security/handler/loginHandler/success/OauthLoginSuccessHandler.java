package store.aurora.config.security.handler.loginHandler.success;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AuthClient;
import store.aurora.feign_client.UserClient;
import store.aurora.feign_client.coupon.CouponClient;
import store.aurora.user.dto.request.SignUpRequest;
import store.aurora.user.exception.UserSignUpFailException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserClient userClient;
    private final AuthClient authClient;
    private final CouponClient couponClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //Oauth2로 로그인한 순간에는 각 idNo가 username임. 이후 다음 요청 부터는 payco:{idNo}형태가 됨. 혹시 로그인 한 순간에 뭔가를 가져올 일이 있다면 주의해야함.
        log.info("authentication username={}", authentication.getName());

        //1. 회원이 있는지 검색
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getName();
        String registrationId = oAuth2User.getAttribute("registrationId");
        String id = String.format("%s:%s", registrationId, username);

        boolean userExists;

        try{
            ResponseEntity<Boolean> feignResponse = userClient.checkUserExists(id);
            userExists = feignResponse != null && Boolean.TRUE.equals(feignResponse.getBody());
        }catch (Exception e){
            log.error("user client 통신 실패. message={}", e.getMessage());
            log.info("", e);
            response.sendRedirect("/login");
            return;
        }


        //2. 회원 가입
        if(!userExists){
            Optional<SignUpRequest> optionalDto = makeSignUpRequest(id, oAuth2User, registrationId);
            if(optionalDto.isEmpty()){
                throw new UserSignUpFailException("missing attributes:" + oAuth2User.getAttributes());
            }
            log.info("회원가입 정보:{}", optionalDto.get());
            ResponseEntity<Map<String, String>> responseEntity = userClient.signUp(optionalDto.get(), true);

//            Map<String, String> result = userClient.signUp(optionalDto.get(), true);
            SignUpRequest signUpRequest = optionalDto.get();
            ResponseEntity<Map<String, String>> mapResponseEntity = userClient.signUp(signUpRequest, true);
            Map<String, String> result = mapResponseEntity.getBody();

            if(responseEntity.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201))){ //todo 201 상수로 변경
                log.info("signup success:{}", responseEntity.getBody().get("message"));
            }
            else {
                log.info("signup fail:{}", responseEntity.getBody().get("message"));
                response.sendRedirect("/login");
                return;
            }

            couponClient.existWelcomeCoupon(id);
        }

        //3. jwt토큰 만들기
        Optional<Cookie> optionalCookie = jwtOven(id);
        if(optionalCookie.isEmpty()){
            log.info("token make fail");
            response.sendRedirect("/login");
            return;
        }
        else {
            log.info("cookie = {}", optionalCookie.get().getValue());
            response.addCookie(optionalCookie.get());
        }

        //4. 필요없어진 액세스토큰과 리프레시 토큰을 만료시킨다?? todo 토론 필요 + 토큰 유효시간 감소 필요성

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
            ResponseEntity<String> jwtToken = authClient.getJwtToken(new JwtRequestDto(id));

            if(!jwtToken.getStatusCode().is2xxSuccessful() || isNullOrBlank(jwtToken.getBody())){
                log.info("response={}", jwtToken);
                return Optional.empty();
            }
            Cookie cookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwtToken.getBody());
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
