package store.aurora.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

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
            String cartCookie = getCartCookieFromRequest();
            if (cartCookie != null) {
                template.header("Cookie", CART_COOKIE_NAME + "=" + cartCookie);
            }

            // Authorization 헤더 추가
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null) {
                template.header("Authorization", authorizationHeader);
            }
        }
    }

    private String getCartCookieFromRequest() {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> CART_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
