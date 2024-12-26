package store.aurora.config.security.handler.loginHandler.success;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feignClient.AuthClient;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthClient authClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //로그인 성공 시 쿠키 만들기
        Optional<Cookie> optionalCookie = jwtOven(authentication.getName());
        if(optionalCookie.isEmpty()){
            log.info("token make fail");
            response.sendRedirect("/login");
            return;
        }
        else {
            response.addCookie(optionalCookie.get());
        }

        //todo 로그인 되고 보낼 곳 정하기
        response.sendRedirect("/login-test");
    }

    private Optional<Cookie> jwtOven(String id){
        try{
            ResponseEntity<String> jwtToken = authClient.getJwtToken(new JwtRequestDto(id));
            if(!jwtToken.getStatusCode().is2xxSuccessful() || isNullOrBlank(jwtToken.getBody())){
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
