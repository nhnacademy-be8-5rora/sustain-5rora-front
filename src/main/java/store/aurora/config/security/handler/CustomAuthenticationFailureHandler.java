package store.aurora.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.aurora.config.security.exception.DormantAccountException;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

//    private static final Logger log = LoggerFactory.getLogger("auth-logger");

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception.getCause() instanceof DormantAccountException) {
//            log.info("휴면 계정 로그인 시도. 리다이렉트 처리.");
            response.sendRedirect("/reactive"); // 휴면 계정 해제 페이지로 이동
        } else {
            response.sendRedirect("/login?error=true"); // 일반 로그인 실패 처리
        }
    }
}
