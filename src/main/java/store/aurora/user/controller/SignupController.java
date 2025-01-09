package store.aurora.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.UserClient;
import store.aurora.user.dto.request.SignUpRequest;

import java.util.Map;

@Controller
@RequestMapping("/signup")
@AllArgsConstructor
public class SignupController {

    private final UserClient userClient;


    @GetMapping
    public String signUp() {
        return "signup";
    }

    @PostMapping
    public String signUp(@ModelAttribute SignUpRequest signUpRequest,
                         HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.signUp(signUpRequest, false);
        return processClientRes(response, clientResponse);
    }

    @PostMapping("/send-code")
    public String sendVerificationCode(@ModelAttribute SignUpRequest signUpRequest,
                                       HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.sendCode(signUpRequest);
        return processClientRes(response, clientResponse);
    }

    @PostMapping("/verify-code")
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
