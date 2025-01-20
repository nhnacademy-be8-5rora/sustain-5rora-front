package store.aurora.config.security.handler.logout_handler.success;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.common.CookieUtil;
import store.aurora.config.security.constants.SecurityConstants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CommonLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //1. 쿠키 지우기
        List<String> cookiesToDelete = Arrays.asList(
                SecurityConstants.TOKEN_COOKIE_NAME,
                SecurityConstants.REFRESH_TOKEN_COOKIE_NAME
        );

        for(String cookie : cookiesToDelete)
            CookieUtil.deleteCookie(cookie, response);

        //2. 리다이렉트
        response.sendRedirect("/");
    }
}