package store.aurora.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.auth.dto.request.PaycoInfoResponse;
import store.aurora.auth.dto.response.PaycoAccessTokenResponse;
import store.aurora.auth.properties.PaycoProperties;
import store.aurora.feignClient.payco.PaycoInfoClient;
import store.aurora.feignClient.payco.PaycoTokenClient;

@Controller
@RequiredArgsConstructor
@Slf4j
public class Oauth2TestController {

    private final PaycoTokenClient paycoTokenClient;
    private final PaycoInfoClient paycoInfoClient;
    private final PaycoProperties paycoProperties;

    @GetMapping("/oauth2-test")
    public String test(@RequestParam("code") String code, Model model) throws JsonProcessingException {

        log.info("client id={}, secret={}, code={}", paycoProperties.getClientId(), paycoProperties.getClientSecret(), code);

        PaycoAccessTokenResponse tokenResponse = paycoTokenClient.getAccessTokenByAuthorizationCode("authorization_code", paycoProperties.getClientId(), paycoProperties.getClientSecret(), code);
        log.info("token response={}", tokenResponse);

        PaycoInfoResponse paycoInfoResponse = paycoInfoClient.getInfoByAccessToken(paycoProperties.getClientId(), tokenResponse.accessToken());
        log.info("info={}", paycoInfoResponse);

        model.addAttribute("idNo", paycoInfoResponse.data().member().idNo());
        model.addAttribute("email", paycoInfoResponse.data().member().email());
        model.addAttribute("mobile", paycoInfoResponse.data().member().mobile());
        model.addAttribute("name", paycoInfoResponse.data().member().name());


        return "login-test";
    }
}
