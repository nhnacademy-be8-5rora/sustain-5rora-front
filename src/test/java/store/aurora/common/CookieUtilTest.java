package store.aurora.common;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class CookieUtilTest {

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
    }

    @Test
    void testAddCookie() {
        // Arrange
        String cookieName = "testCookie";
        String cookieValue = "testValue";

        // Act
        CookieUtil.addCookie(response, cookieName, cookieValue);

        // Assert
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        assertEquals(cookieName, cookies[0].getName());
        assertEquals(cookieValue, cookies[0].getValue());
        assertEquals("/", cookies[0].getPath());
        assertTrue(cookies[0].isHttpOnly());
        assertEquals(60 * 60 * 24, cookies[0].getMaxAge());
    }

    @Test
    void testDeleteCookie() {
        // Arrange
        String cookieName = "testCookie";

        // Act
        CookieUtil.deleteCookie(cookieName, response);

        // Assert
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        assertEquals(cookieName, cookies[0].getName());
        assertNull(cookies[0].getValue());
        assertEquals(0, cookies[0].getMaxAge());
        assertEquals("/", cookies[0].getPath());
    }

    @Test
    void testGetCookie() {
        // Arrange
        String cookieName = "testCookie";
        String cookieValue = "testValue";
        Cookie cookie = new Cookie(cookieName, cookieValue);
        request.setCookies(cookie);

        // Act
        String result = CookieUtil.getCookie(request, cookieName);

        // Assert
        assertNotNull(result);
        assertEquals(cookieValue, result);
    }

    @Test
    void testGetCookie_NoCookieFound() {
        // Arrange
        String cookieName = "missingCookie";

        // Act
        String result = CookieUtil.getCookie(request, cookieName);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCookie_WithEmptyValue() {
        // Arrange
        String cookieName = "emptyCookie";
        Cookie cookie = new Cookie(cookieName, "");
        request.setCookies(cookie);

        // Act
        String result = CookieUtil.getCookie(request, cookieName);

        // Assert
        assertNull(result);
    }

    @Test
    void testOvenCookie() {
        // Arrange
        String cookieName = "ovenCookie";
        String cookieValue = "freshlyBaked";

        // Act
        Cookie cookie = CookieUtil.ovenCookie(cookieName, cookieValue);

        // Assert
        assertNotNull(cookie);
        assertEquals(cookieName, cookie.getName());
        assertEquals(cookieValue, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(60 * 60 * 24, cookie.getMaxAge());
    }
}