package store.aurora.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.common.annotation.Auth;

//todo 제거 예정
@Controller
public class TestController {

    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    @GetMapping("/login-test")
    public String userIdTest(@Auth String username, Model model){
        model.addAttribute("username", username);
        USER_LOG.error("test error");
        return "login-test";
    }

    @GetMapping("/")
    public String indexTest(){
        USER_LOG.error("5rora send error test");
        return "index";
    }
}
