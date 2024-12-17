package store.aurora.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.feignClient.UserClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtToken = getJwtToken(request);

        if(jwtToken.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        //토큰을 가지고 api 호출
        UserUsernameAndRoleResponse usernameAndRole = userClient.getUsernameAndRole(jwtToken.get());

        SecurityContextHolder.getContext().setAuthentication(makeAuthentication(usernameAndRole));

        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtToken(HttpServletRequest request){
        String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)){
            return Optional.of(authHeader);
        }

        return Optional.empty();
    }

    private UsernamePasswordAuthenticationToken makeAuthentication(UserUsernameAndRoleResponse usernameAndRole){
        return new UsernamePasswordAuthenticationToken(
                usernameAndRole.username(),
                null,
                List.of(new SimpleGrantedAuthority(usernameAndRole.role().name()))
        );
    }
}
