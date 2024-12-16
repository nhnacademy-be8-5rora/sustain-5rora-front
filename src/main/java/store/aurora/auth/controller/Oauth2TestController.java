package store.aurora.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.auth.dto.request.PaycoInfoRequest;
import store.aurora.auth.dto.request.PaycoInfoResponse;
import store.aurora.auth.dto.response.PaycoAccessTokenResponse;
import store.aurora.auth.properties.PaycoProperties;
import store.aurora.feignClient.payco.PaycoInfoClient;
import store.aurora.feignClient.payco.PaycoTokenClient;

@Controller
@RequiredArgsConstructor
public class Oauth2TestController {

    private final PaycoTokenClient paycoTokenClient;
    private final PaycoInfoClient paycoInfoClient;
    private final PaycoProperties paycoProperties;

    @GetMapping("/oauth2-test")
    public String test(@RequestParam("code") String code, Model model){

        PaycoAccessTokenResponse tokenResponse = paycoTokenClient.getAccessTokenByAuthorizationCode("authorization_code", paycoProperties.getClientId(), paycoProperties.getClientSecret(), code);
        PaycoInfoResponse paycoInfoResponse = paycoInfoClient.getInfoByAccessToken(new PaycoInfoRequest(paycoProperties.getClientId(), tokenResponse.accessToken()));

        model.addAttribute("idNo", paycoInfoResponse.data().idNo());
        model.addAttribute("email", paycoInfoResponse.data().email());
        model.addAttribute("mobile", paycoInfoResponse.data().mobile());
        model.addAttribute("name", paycoInfoResponse.data().name());


        return "login-test";
    }
}
