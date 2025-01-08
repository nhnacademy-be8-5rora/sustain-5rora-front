package store.aurora.feignClient.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.dto.SubmitResponse;

@FeignClient(name = "orderTemporaryStorageClient", url = "${api.gateway.base-url}" + "/api/order")
public interface OrderTemporaryStorageClient {

    @PostMapping("/save-order-info")
    ResponseEntity<SubmitResponse> saveOrderInfo(@RequestBody OrderRequestDto orderRequestDto);

    @GetMapping("/{order-id}/get-order-info")
    ResponseEntity<OrderResponseDto> getOrderInfo(@PathVariable(name="order-id") String orderId);
}
