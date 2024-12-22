package store.aurora.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import store.aurora.config.security.constants.SecurityConstants;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

//@Component
public class CookieToHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<String> cookieValue = getCookieValue(request);
        cookieValue.ifPresent(s -> response.setHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_TOKEN_PREFIX + s));

        filterChain.doFilter(request, response);
    }

    private Optional<String> getCookieValue(HttpServletRequest request){

        if (Objects.isNull(request.getCookies())) {
            return Optional.empty();
        }

        for (Cookie cookie : request.getCookies()) {
            if(
                    cookie.getName().equals(SecurityConstants.TOKEN_COOKIE_NAME)
                    && Objects.nonNull(cookie.getValue())
                    && !cookie.getValue().isEmpty()
                    && !cookie.getValue().isBlank()
            ){
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
