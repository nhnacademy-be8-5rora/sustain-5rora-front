package store.aurora.config.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.aurora.common.JwtUtil;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.UserClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandlerUtil {

    private final UserClient userClient;
    private final JwtUtil jwtUtil;

    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    public void handleSuccess(HttpServletResponse response, String username) throws IOException {
        //로그인 성공 시 쿠키 만들기
        Optional<Cookie> optionalCookie = jwtUtil.jwtOven(SecurityConstants.TOKEN_COOKIE_NAME, username, null); //60000L); // 1분 / 10분 600000
        Optional<Cookie> refresh = jwtUtil.jwtOven(SecurityConstants.REFRESH_TOKEN_COOKIE_NAME, username, 86400000L); // 2분 120000L / 86400000L 24시간로 하기

        if(optionalCookie.isEmpty()){
            LOG.error("access token make fail");
            response.sendRedirect("/login");
            return;
        }
        else {
            LOG.info("cookie = {}", optionalCookie.get().getValue());
            response.addCookie(optionalCookie.get());
        }

        if(refresh.isEmpty()){
            LOG.error("refresh token make fail");
            response.sendRedirect("/login");
            return;
        }
        else {
            response.addCookie(refresh.get());
        }

        // 마지막 로그인 날짜 갱신
        userClient.updateLastLogin(username, LocalDateTime.now());

        //4. 필요없어진 액세스토큰과 리프레시 토큰을 만료시킨다?? todo 토론 필요 + 토큰 유효시간 감소 필요성

        response.sendRedirect("/");
    }
}