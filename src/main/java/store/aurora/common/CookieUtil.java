package store.aurora.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

public class CookieUtil {
    private CookieUtil() {}

    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
        response.addCookie(
                CookieUtil.ovenCookie(cookieName, cookieValue)
        );
    }

    public static Cookie ovenCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // 보안을 위해 HttpOnly 설정 (JS에서 접근 불가)
        cookie.setMaxAge(60 * 60 * 24);  // 24시간
        return cookie;
    }

    public static void deleteCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); // Expire immediately
        cookie.setPath("/"); // Ensure it applies to the entire domain
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        // 쿠키가 존재하지 않거나, 쿠키 배열이 비어있는 경우
        if (cookies == null) {
            return null;
        }

        // 쿠키 배열을 순회하며 원하는 쿠키를 찾음
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)
                    && Objects.nonNull(cookie.getValue())
            ) {
                return cookie.getValue();  // 쿠키 이름이 일치하면 반환
            }
        }

        return null;  // 해당 이름의 쿠키가 없으면 null 반환
    }
}