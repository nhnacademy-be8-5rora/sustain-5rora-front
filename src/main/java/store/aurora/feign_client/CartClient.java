package store.aurora.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.cart.dto.CartItemDTO;

import java.util.Map;

@FeignClient(name = "cartClient", url = "${api.gateway.base-url}" + "/api/cart")
public interface CartClient {
    @GetMapping
    ResponseEntity<Map<String, Object>> getCart();

    @PostMapping
    ResponseEntity<String> addCartItem(@RequestBody CartItemDTO cartItemDTO);

    @DeleteMapping("/{bookId}")
    ResponseEntity<String> deleteCartItem(@PathVariable("bookId") Long bookId);
}
