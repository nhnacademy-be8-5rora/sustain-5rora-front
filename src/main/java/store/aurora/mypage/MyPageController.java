package store.aurora.mypage;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.UserClient;

import java.util.Map;

@Controller
@RequestMapping("/mypage")
@AllArgsConstructor
public class MyPageController {

    private final UserClient userClient;

    @GetMapping
    public String mypage(Authentication authentication,
                         Model model) {
        model.addAttribute("userId", authentication.getPrincipal().toString());
        return "mypage/mypage";
    }

    // 회원탈퇴
    @PostMapping("delete/{userId}")
    public String deleteUser(@PathVariable(value = "userId") String userId,
                             HttpServletResponse response) {
        ResponseEntity<Map<String, String>> clientResponse = userClient.deleteUser(userId);
        return processClientRes(response, clientResponse);
    }

    private String processClientRes(HttpServletResponse response, ResponseEntity<Map<String, String>> clientResponse) {
        HttpHeaders headers = clientResponse.getHeaders();
        String sessionCookie = headers.getFirst("Set-Cookie");

        if (sessionCookie != null) {
            response.addHeader("Set-Cookie", sessionCookie);
        }

        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            return "redirect:/logout";
        } else {
            return "redirect:/error";
        }
    }
}
