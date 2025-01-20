package store.aurora.config.security.handler.login_handler.success;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.aurora.config.security.handler.LoginSuccessHandlerUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginSuccessHandlerUtil loginSuccessHandlerUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        loginSuccessHandlerUtil.handleSuccess(response, authentication.getName());
    }
}