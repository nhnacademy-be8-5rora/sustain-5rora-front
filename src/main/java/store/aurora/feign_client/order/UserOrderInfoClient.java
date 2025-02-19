package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;

@FeignClient(name = "userOrderInfoClient", url = "${api.gateway.base-url}")
public interface UserOrderInfoClient {

    @GetMapping("/api/users/{user-id}/orders")
    ResponseEntity<Page<OrderInfoDto>> getOrderInfos(@PathVariable("user-id") String encrytedId, Pageable pageable);

    @GetMapping("/api/users/{user-id}/orders/{order-id}")
    ResponseEntity<OrderWithOrderDetailResponse> getOrderWithOrderDetails(@PathVariable("user-id") String encryptedUserId,
                                                                          @PathVariable("order-id") String encryptedOrderId);
}
