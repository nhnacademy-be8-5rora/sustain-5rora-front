package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.feign_client.order.GenericUserOrderClient;
import store.aurora.order.dto.OrderDetailInfoDto;
import store.aurora.order.dto.OrderRelatedInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order/non-member")
public class NonMemberOrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final SimpleEncryptor simpleEncryptor;
    private final GenericUserOrderClient genericUserOrderClient;

    @GetMapping("/search")
    public String nonMemberSearchForm(){
        return "order/order-search";
    }

    @PostMapping("/search")
    public String nonMemberSearch(@RequestParam("order-id")Long orderId,
                                  @RequestParam("password")String password){

        String code = simpleEncryptor.encrypt(String.format("%s:%s", orderId, password));

        return "redirect:/order/non-member/orders/" + code;
    }

    @GetMapping("/orders/{code}")
    public String nonMemberDetails(@PathVariable("code") String code,
                                   Model model){
        OrderWithOrderDetailResponse orderWithOrderDetailResponse = genericUserOrderClient.getNonMemberOrderInfo(code).getBody();
        OrderRelatedInfoDto orderInfo = orderWithOrderDetailResponse.getOrderRelatedInfoDto();
        List<OrderDetailInfoDto> orderDetailInfos = orderWithOrderDetailResponse.getOrderDetailInfoDtoList();

        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("orderDetails", orderDetailInfos);

        return "order/order-detail-non-member";
    }
}
