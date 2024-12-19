package store.aurora.feignClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.cart.dto.CartItemDTO;

import java.io.UnsupportedEncodingException;
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
