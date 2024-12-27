package store.aurora.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import store.aurora.config.security.constants.SecurityConstants;

import java.util.Arrays;

@Component
public class CookieFeignInterceptor implements RequestInterceptor {
    private static final String CART_COOKIE_NAME = "CART";
    private final HttpServletRequest request;

    // HttpServletRequest는 현재 요청에서 쿠키를 가져오기 위해 주입
    public CookieFeignInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {
        if (request.getRequestURI().startsWith("/cart")) {
            // 쿠키 추가
            String cartCookie = getCookieFromRequest(CART_COOKIE_NAME);
            if (cartCookie != null) {
                template.header("Cookie", CART_COOKIE_NAME + "=" + cartCookie);
            }

            // Authorization 쿠키 추가
            String authorizationCookie = getCookieFromRequest(SecurityConstants.TOKEN_COOKIE_NAME);
            if (authorizationCookie != null) {
                template.header(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_TOKEN_PREFIX + authorizationCookie);
            }
        }
    }

    private String getCookieFromRequest(String cookieName) { // todo 통합 : 여기저기 있음
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
