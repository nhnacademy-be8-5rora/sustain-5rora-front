package store.aurora.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feignClient.UserClient;
import store.aurora.user.dto.request.SignUpRequest;

import java.util.Map;

@Controller
@AllArgsConstructor
public class SignupController {
    private final UserClient userClient;


    @PostMapping
    public String signUp(@ModelAttribute SignUpRequest signUpRequest,
                         @RequestParam boolean isOauth,
                         HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.signUp(signUpRequest, isOauth);
        return processClientRes(response, clientResponse);
    }

    @PostMapping("/signup/send-code")
    public String sendVerificationCode(@ModelAttribute SignUpRequest signUpRequest,
                                       HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.sendCode(signUpRequest);
        return processClientRes(response, clientResponse);
    }

    @PostMapping("/signup/verify-code")
    public String verifyCode(@ModelAttribute SignUpRequest signUpRequest,
                             HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.verifyCode(signUpRequest);
        return processClientRes(response, clientResponse);
    }

    private String processClientRes(HttpServletResponse response, ResponseEntity<Map<String, String>> clientResponse) {
        HttpHeaders headers = clientResponse.getHeaders();
        String sessionCookie = headers.getFirst("Set-Cookie");

        if (sessionCookie != null) {
            response.addHeader("Set-Cookie", sessionCookie);
        }

        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            return "redirect:/login";
        } else {
            return "redirect:/error";
        }
    }
}
