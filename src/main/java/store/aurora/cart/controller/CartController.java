package store.aurora.cart.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.common.CookieUtil;
import store.aurora.feignClient.CartClient;

import java.util.Map;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private final CartClient cartClient;

    @GetMapping
    public String getCartPage(Model model) {
        ResponseEntity<Map<String, Object>> response = cartClient.getCart();
        Map<String, Object> cartData = response.getBody();
        model.addAttribute("cart", cartData);
        return "cart";
    }

    @PostMapping
    public String addCart(@RequestParam Long bookId,
                          @RequestParam Integer quantity,
                          HttpServletResponse response) {
        ResponseEntity<String> clientResponse = cartClient.addCartItem(new CartItemDTO(bookId, quantity));
        return processClientRes(response, clientResponse);
    }

    @DeleteMapping("/{bookId}")
    public String deleteItemToCart(@PathVariable(value = "bookId", required = false) Long bookId,
                                   HttpServletResponse response) {

        ResponseEntity<String> clientResponse = cartClient.deleteCartItem(bookId);
        return processClientRes(response, clientResponse);
    }

    private String processClientRes(HttpServletResponse response, ResponseEntity<String> clientResponse) {
        HttpHeaders headers = clientResponse.getHeaders();
        String cartCookie = headers.getFirst("Set-Cookie");

        if (cartCookie != null) {
            response.addHeader("Set-Cookie", cartCookie);
        }

        if (clientResponse.getStatusCode().is2xxSuccessful()) {
            return "redirect:/cart";
        } else {
            return "redirect:/error";
        }
    }
}
