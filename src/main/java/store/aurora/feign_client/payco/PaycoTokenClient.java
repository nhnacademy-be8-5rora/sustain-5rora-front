package store.aurora.feign_client.payco;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.auth.dto.response.PaycoAccessTokenResponse;

@FeignClient(name = "paycoTokenClient", url = "https://id.payco.com")
public interface PaycoTokenClient {

    //authorization code 로 발급하는 경우 grant_type="authorization_code"
    @GetMapping("/oauth2.0/token")
    PaycoAccessTokenResponse getAccessTokenByAuthorizationCode(
                                            @RequestParam(value = "grant_type", defaultValue = "authorization_code") String grantType,
                                            @RequestParam("client_id") String clientId,
                                            @RequestParam("client_secret") String clientSecret,
                                            @RequestParam("code") String code
    );

    //refresh_token 으로 발급하는 경우 grant_type="refresh_token"
    @GetMapping("/oauth2.0/token")
    PaycoAccessTokenResponse getAccessTokenByRefreshToken(
                                            @RequestParam(value = "grant_type", defaultValue = "refresh_token") String grantType,
                                            @RequestParam("client_id") String clientId,
                                            @RequestParam("client_secret") String clientSecret,
                                            @RequestParam("refresh_token") String refreshToken
    );

    //todo 토큰 삭제 api 추가?
}
