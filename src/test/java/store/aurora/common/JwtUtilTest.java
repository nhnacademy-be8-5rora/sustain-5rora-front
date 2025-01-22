package store.aurora.common;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AuthClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void testGetJwtFromCookie() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwtValue = "test-jwt";
        String expectedJwt = "Bearer " + jwtValue;
        request.setCookies(new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwtValue));

        // Act
        String result = JwtUtil.getJwtFromCookie(request);

        // Assert
        assertEquals(expectedJwt, result);
    }

    @Test
    void testGetJwtFromCookie_NoCookie() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        String result = JwtUtil.getJwtFromCookie(request);

        // Assert
        assertEquals("Bearer null", result); // If no cookie is found, it should return "Bearer null".
    }

    @Test
    void testJwtOven_Success() {
        // Arrange
        String category = "testCategory";
        String id = "12345";
        Long expiredMs = 3600000L;
        String jwtBody = "jwt-token";
        ResponseEntity<String> mockResponse = ResponseEntity.ok(jwtBody);

        when(authClient.getJwtToken(new JwtRequestDto(id, expiredMs))).thenReturn(mockResponse);

        // Act
        Optional<Cookie> result = jwtUtil.jwtOven(category, id, expiredMs);

        // Assert
        assertTrue(result.isPresent());
        Cookie cookie = result.get();
        assertEquals(category, cookie.getName());
        assertEquals(jwtBody, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void testJwtOven_Failure_NullBody() {
        // Arrange
        String category = "testCategory";
        String id = "12345";
        Long expiredMs = 3600000L;

        ResponseEntity<String> mockResponse = ResponseEntity.ok(null);

        when(authClient.getJwtToken(new JwtRequestDto(id, expiredMs))).thenReturn(mockResponse);

        // Act
        Optional<Cookie> result = jwtUtil.jwtOven(category, id, expiredMs);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testJwtOven_Failure_Exception() {
        // Arrange
        String category = "testCategory";
        String id = "12345";
        Long expiredMs = 3600000L;

        when(authClient.getJwtToken(new JwtRequestDto(id, expiredMs))).thenThrow(new RuntimeException("Test exception"));

        // Act
        Optional<Cookie> result = jwtUtil.jwtOven(category, id, expiredMs);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testIsNullOrBlank() {
        // Arrange & Act & Assert
        assertTrue(JwtUtil.isNullOrBlank(null));
        assertTrue(JwtUtil.isNullOrBlank(""));
        assertTrue(JwtUtil.isNullOrBlank(" "));
        assertFalse(JwtUtil.isNullOrBlank("test"));
    }
}