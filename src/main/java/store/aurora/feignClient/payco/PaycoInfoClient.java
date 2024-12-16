package store.aurora.feignClient.payco;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import store.aurora.auth.dto.request.PaycoInfoResponse;

@FeignClient(name = "paycoInfoClient", url = "https://apis-payco.krp.toastoven.net")
public interface PaycoInfoClient {

    @PostMapping("/payco/friends/find_member_v2.json")
    PaycoInfoResponse getInfoByAccessToken(@RequestHeader("client_id")String clientId,
                                @RequestHeader("access_token") String accessToken);
}
