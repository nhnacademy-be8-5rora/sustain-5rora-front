package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.OrderCompleteRequestDto;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.dto.SubmitResponse;

@FeignClient(name = "orderTemporaryStorageClient", url = "${api.gateway.base-url}" + "/api/order")
public interface OrderTemporaryStorageClient {

    @PostMapping("/save-order-info")
    ResponseEntity<SubmitResponse> saveOrderInfo(@RequestBody OrderRequestDto orderRequestDto);

    @GetMapping("/{order-id}/get-order-info")
    ResponseEntity<OrderResponseDto> getOrderInfo(@PathVariable(name="order-id") String orderId);

//    @PostMapping("/order-complete")
//    ResponseEntity<Void> orderComplete(@RequestBody OrderCompleteRequestDto orderCompleteRequestDto);
}
