package store.aurora.common;

import jakarta.servlet.http.HttpServletRequest;
import store.aurora.config.security.constants.SecurityConstants;

public class JwtUtil {
    public static String getJwtFromCookie(HttpServletRequest request) {
        return SecurityConstants.BEARER_TOKEN_PREFIX + CookieUtil.getCookie(request, SecurityConstants.TOKEN_COOKIE_NAME);
    }
}
