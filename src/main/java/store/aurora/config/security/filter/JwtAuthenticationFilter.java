package store.aurora.config.security.filter;

import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtToken = getCookieValue(request);

        if(jwtToken.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        //토큰을 가지고 api 호출
        ResponseEntity<UserUsernameAndRoleResponse> usernameAndRole = null;
        try{
            usernameAndRole = userClient.getUsernameAndRole(SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken.get());
        }catch (FeignException e){
            log.error("통신 실패 message={}", e.getMessage());
            response.sendRedirect("/login");  //todo 에러 페이지 보내기?
            return;
        }

        if(Objects.isNull(usernameAndRole)){
            log.error("usernameAndRole is null");
            response.sendRedirect("/login"); //todo 에러 페이지 보내기?
            return;
        }

        //todo 상태코드에 따른 처리
        HttpStatusCode statusCode = usernameAndRole.getStatusCode();
        if(statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))){
            log.info("not found user:{}", SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken.get());
            filterChain.doFilter(request, response);
            return;
        }
        else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
            log.info("status code={}", statusCode.value());
            response.sendRedirect("/login");   //todo 에러 페이지 보내기?
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(makeAuthentication(usernameAndRole.getBody()));

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken makeAuthentication(UserUsernameAndRoleResponse usernameAndRole){
        return new UsernamePasswordAuthenticationToken(
                usernameAndRole.username(),
                null,
                List.of(new SimpleGrantedAuthority(usernameAndRole.role().name()))
        );
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
