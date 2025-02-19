package store.aurora.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.UserClient;
import store.aurora.feign_client.coupon.CouponClient;
import store.aurora.user.dto.request.SignUpRequest;
import store.aurora.user.dto.request.VerificationRequest;

import java.util.Map;

@Controller
@RequestMapping
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;
    private final CouponClient couponClient;

    @GetMapping("/signup")
    public String signUp() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute SignUpRequest signUpRequest,
                         HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.signUp(signUpRequest, false);

        String id = signUpRequest.getId();

        couponClient.signUpWelcomeCoupon(id);

        return processClientRes(response, clientResponse);
    }

    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestParam String phoneNumber,
                                       Model model) {
        try {
            ResponseEntity<Map<String, String>> clientResponse = userClient.sendCode(phoneNumber);
            model.addAttribute("phoneNumber", phoneNumber);
            model.addAttribute("sendMessage", clientResponse.getBody().get("message"));
            model.addAttribute("messageColor", "green");
        } catch (FeignException e) {
            model.addAttribute("sendMessage", "인증번호 전송에 실패했습니다.");
            model.addAttribute("messageColor", "red");
            return "reactive";
        }
        return "reactive";
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam String phoneNumber,
                             @RequestParam String verificationCode,
                             Model model) {
        VerificationRequest request = new VerificationRequest(phoneNumber, verificationCode);

        try {
            ResponseEntity<Map<String, String>> clientResponse = userClient.verifyCode(request);
            model.addAttribute("verifyMessage", clientResponse.getBody().get("message"));
            model.addAttribute("messageColor", "green");
        } catch(FeignException e) {
            try {
                // FeignException의 응답 본문을 UTF-8로 가져옵니다.
                String responseBody = e.contentUTF8();

                // ObjectMapper를 사용하여 JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // message 필드를 추출
                String message = jsonNode.get("message").asText();

                // 모델에 메시지를 추가
                model.addAttribute("verifyMessage", message);
                model.addAttribute("messageColor", "red");
            } catch (Exception ex) {
                model.addAttribute("verifyMessage", "에러 발생: " + ex.getMessage());
                model.addAttribute("messageColor", "red");
            }
        }
        return "reactive";
    }

    @PostMapping("/reactivate")
    public String reactivateUser(@RequestParam String userId,
                                 HttpServletResponse response) {
        System.out.println("Received userId: " + userId); // 디버깅용 출력

        if (userId == null || userId.isEmpty()) {
            return "error";  // 오류 페이지로 리턴
        }

        ResponseEntity<Map<String, String>> clientResponse = userClient.reactivateUser(userId);

        return processClientRes(response, clientResponse);
    }

    private String processClientRes(HttpServletResponse response, ResponseEntity<Map<String, String>> clientResponse) {
        HttpHeaders headers = clientResponse.getHeaders();
        String sessionCookie = headers.getFirst("Set-Cookie");

        if (sessionCookie != null) {
            response.addHeader("Set-Cookie", sessionCookie);
        }
        return "redirect:/login";
    }
}
