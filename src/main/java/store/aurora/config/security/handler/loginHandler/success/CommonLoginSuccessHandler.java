package store.aurora.config.security.handler.loginHandler.success;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feignClient.AuthClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CommonLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthClient authClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //로그인 성공 시 쿠키 만들기
        String jwtToken = authClient.getJwtToken(new JwtRequestDto(authentication.getName()));
        Cookie cookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwtToken);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        //todo 로그인 되고 보낼 곳 정하기
        response.sendRedirect("/");
    }
}
