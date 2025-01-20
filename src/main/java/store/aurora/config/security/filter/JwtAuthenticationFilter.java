package store.aurora.config.security.filter;

import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import store.aurora.common.CookieUtil;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.feign_client.AuthClient;
import store.aurora.feign_client.UserClient;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserClient userClient;
    private final AuthClient authClient; // AuthClient를 추가하여 새 토큰 요청
    private static final String LOGIN_URL = "/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = CookieUtil.getCookie(request, SecurityConstants.TOKEN_COOKIE_NAME);

        if(jwtToken == null || request.getRequestURI().equals(LOGIN_URL) || request.getRequestURI().equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰을 가지고 api 호출
        ResponseEntity<UserUsernameAndRoleResponse> usernameAndRole = null;
        try{
            usernameAndRole = userClient.getUsernameAndRole(SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken);
        }catch (FeignException e){
            if (e.status() == HttpStatus.UNAUTHORIZED.value()) { // todo FeignClientException (=4xx) 에 잡히면 무조건 갱신 로직 실행하도록? (getUsernameAndRole 요청 시 shop api 에서 404 올 수도 있어서)
                // 리프레시 토큰을 활용한 JWT 갱신 처리
                String refreshToken = CookieUtil.getCookie(request, SecurityConstants.REFRESH_TOKEN_COOKIE_NAME);
                if (refreshToken != null) {
                    try {
                        String newJwt = authClient.refreshToken(refreshToken);
                        CookieUtil.addCookie(response, SecurityConstants.TOKEN_COOKIE_NAME, newJwt); // 새 JWT를 쿠키로 저장
                        String requestURI = request.getRequestURI();
                        log.debug("리프레시 토큰으로 jwt 재발급 후 리다렉트 되는 url : {} (access 토큰 갱신돼있어야함)", requestURI);
                        response.sendRedirect(requestURI);
                        return;
                    } catch (Exception ex) {
                        log.error("리프레시 토큰으로 jwt 재발급 실패 : ", e); // 첫 번째 원인 : refresh 토큰 만료
                        response.sendRedirect("/logout");
                        return;
                    }
                } else {
                    response.sendRedirect(LOGIN_URL);
                    return;
                }
            } else {
                log.error("jwt로 getUsernameAndRole 실패 : {}", e.getMessage(), e);
                response.sendRedirect(LOGIN_URL); //todo 에러 페이지 보내기?
                return;
            }

            /*todo 상태코드에 따른 처리 (단, 메서드로 분리 안하면 소나큐브 정적 분석 빨간색 뜸)
            HttpStatusCode statusCode = usernameAndRole.getStatusCode();
            if(statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))){
                log.info("not found user:{}", SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken);
                filterChain.doFilter(request, response);
                return;
            }
            else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                log.info("status code={}", statusCode.value());
                response.sendRedirect(LOGIN_URL);   //todo 에러 페이지 보내기?
                return;
            }*/
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
}