package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order/non-member")
public class NonMemberOrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    @GetMapping("/search")
    public String nonMemberSearchForm(){
        return "order/order-search";
    }

    @PostMapping("/search")
    public String nonMemberSearch(@RequestParam("order-id")Long orderId,
                                  @RequestParam("password")String password){
        return "redirect:/order/non-member/orders/" + orderId;
    }

    @GetMapping("/orders/{order-id}")
    public String nonMemberDetails(@PathVariable("order-id") Long orderId){
        return null;
    }
}
