package store.aurora.config.security.handler.logoutHandler.success;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.config.security.constants.SecurityConstants;

import java.io.IOException;

@Component
public class CommonLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //1. 쿠키 지우기
        Cookie cookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        //2. 리다이렉트
        response.sendRedirect("/");
    }
}
