package store.aurora.order.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.common.annotation.Auth;
import store.aurora.feign_client.order.UserOrderInfoClient;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.exception.UserOrderInfoClientResolveFailException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/orders")
public class UserOrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserOrderInfoClient userOrderInfoClient;

    @GetMapping
    public String myOrderPages(@Auth String userId, Pageable pageable, Model model){

        ResponseEntity<Page<OrderInfoDto>> orderInfosResponse;
        try {
            orderInfosResponse = userOrderInfoClient.getOrderInfos(userId, pageable);
        }catch (FeignException e){
            throw new UserOrderInfoClientResolveFailException(e);
        }
        Page<OrderInfoDto> pagedOrderInfo = orderInfosResponse.getBody();
        log.info("회원{}의 주문목록:{}", userId, pagedOrderInfo.getContent());

        model.addAttribute("pagedOrderInfo", pagedOrderInfo);

        return "order/order-list";
    }

    @GetMapping("/{order-id}")
    public String orderDetailPage(@PathVariable("order-id") Long orderId,
                                  @Auth String userId,
                                  Model model){
        //userId 검증?
        //todo 주문 상세 페이지 작성
        return null;
    }
}
