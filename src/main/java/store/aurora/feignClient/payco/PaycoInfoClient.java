package store.aurora.feignClient.payco;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import store.aurora.auth.dto.request.PaycoInfoRequest;
import store.aurora.auth.dto.request.PaycoInfoResponse;

@FeignClient(name = "paycoInfoClient", url = "https://apis-payco.krp.toastoven.net")
public interface PaycoInfoClient {

    @PostMapping
    PaycoInfoResponse getInfoByAccessToken(PaycoInfoRequest paycoInfoRequest);
}
