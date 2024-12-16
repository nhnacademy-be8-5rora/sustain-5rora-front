package store.aurora.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Oauth2TestController {

    @GetMapping("/oauth2-test")
    public String test(@RequestParam("code") String code, Model model){

        model.addAttribute("code", code);

        return "login-test";
    }
}
