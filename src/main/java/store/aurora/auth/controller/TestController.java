package store.aurora.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.common.annotation.Auth;

//todo 제거 예정
@Controller
public class TestController {

    @GetMapping("/login-test")
    public String userIdTest(@Auth String username, Model model){
        model.addAttribute("username", username);
        return "login-test";
    }
}
