package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "genericUserOrderClient", url = "${api.gateway.base-url}")
public interface GenericUserOrderClient {
    @GetMapping("/api/order/verify")
    ResponseEntity<Boolean> isOwner(@RequestParam("order-id") String encryptedOrderId,
                                    @RequestParam(value = "user-id", required = false) String encryptedUserId, //비회원의 경우 null
                                    @RequestParam(value = "encryptedPassword", required = false) String encryptedPassword); //회원의 경우 null

    @PostMapping("/api/order/cancel")
    ResponseEntity<Long> orderCancel(@RequestParam("order-id") String encryptedOrderId);

    @PostMapping("/api/order/refund")
    ResponseEntity<Long> requestRefund(@RequestParam("order-id") String encryptedOrderId);
}
