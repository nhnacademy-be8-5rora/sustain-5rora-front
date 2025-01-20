package store.aurora.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.AuthClient;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final AuthClient authClient;
    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    public static String getJwtFromCookie(HttpServletRequest request) {
        return SecurityConstants.BEARER_TOKEN_PREFIX + CookieUtil.getCookie(request, SecurityConstants.TOKEN_COOKIE_NAME);
    }

    public Optional<Cookie> jwtOven(String category, String id, Long expiredMs) {
        try {
            ResponseEntity<String> jwtToken = authClient.getJwtToken(new JwtRequestDto(id, expiredMs));
            if (isNullOrBlank(jwtToken.getBody())) {
                LOG.error("Failed jwtOven = {}", jwtToken);
                return Optional.empty();
            }
            return Optional.of(CookieUtil.ovenCookie(category, jwtToken.getBody()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean isNullOrBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }
}