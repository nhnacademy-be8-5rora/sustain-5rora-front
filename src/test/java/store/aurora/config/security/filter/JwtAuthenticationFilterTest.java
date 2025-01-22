package store.aurora.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AuthClient;
import store.aurora.feign_client.UserClient;
import store.aurora.user.domain.Role;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private UserClient userClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws Exception {
        // Arrange
        String jwtToken = "valid-jwt";
        Cookie jwtCookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwtToken);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(request.getRequestURI()).thenReturn("/some-secure-endpoint");

        UserUsernameAndRoleResponse mockUserResponse = new UserUsernameAndRoleResponse("user", Role.ROLE_USER);
        when(userClient.getUsernameAndRole(SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken))
                .thenReturn(ResponseEntity.ok(mockUserResponse));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterInternal_NoToken() throws Exception {
        // Arrange
        when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/some-secure-endpoint");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_LogoutOrLoginPath() throws Exception {
        // Arrange
        when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/logout");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}