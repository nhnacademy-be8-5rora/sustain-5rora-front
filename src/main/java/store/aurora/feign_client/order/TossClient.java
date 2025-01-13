package store.aurora.feign_client.order;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.PaymentRequest;

@FeignClient(name = "tossClient", url = "https://api.tosspayments.com")
public interface TossClient {

    @PostMapping("/v1/payments/confirm")
    ResponseEntity<String> confirmCheck(@RequestHeader("Authorization") String authorization, @RequestBody PaymentRequest paymentRequest);

    @GetMapping("/v1/payments/{paymentKey}")
    ResponseEntity<String> searchPayment(@RequestHeader("Authorization") String authorization, @PathVariable("paymentKey") String paymentKey);
}
