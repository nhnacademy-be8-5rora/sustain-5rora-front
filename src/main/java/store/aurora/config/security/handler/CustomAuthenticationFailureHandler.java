package store.aurora.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import store.aurora.config.security.exception.DormantAccountException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다.";

        if (exception.getCause() instanceof DormantAccountException) {
//            errorMessage = "휴면계정입니다. 휴면 해제하시겠습니까?";
            response.sendRedirect("/reactive"); // 휴면 계정 해제 페이지로 이동
            return;
        } else if (exception.getMessage().contains("탈퇴")) {
            errorMessage = "탈퇴한 계정입니다.";
        }

        String redirectUrl = "/login?error=true&message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);

    }
}
