package store.aurora.order.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.common.annotation.Auth;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.feign_client.order.GenericUserOrderClient;
import store.aurora.feign_client.order.UserOrderInfoClient;
import store.aurora.order.dto.OrderDetailInfoDto;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderRelatedInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.exception.UserOrderInfoClientResolveFailException;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserOrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserOrderInfoClient userOrderInfoClient;
    private final SimpleEncryptor simpleEncryptor;
    private final GenericUserOrderClient genericUserOrderClient;

    @GetMapping("/mypage/orders")
    public String myOrderPages(@Auth String userId, Pageable pageable, Model model){

        ResponseEntity<Page<OrderInfoDto>> orderInfosResponse;
        String encrypted = simpleEncryptor.encrypt(userId);
        log.info("encrypted user id = {}", encrypted);
        try {
            orderInfosResponse = userOrderInfoClient.getOrderInfos(encrypted, pageable);
        }catch (FeignException e){
            throw new UserOrderInfoClientResolveFailException(e);
        }
        Page<OrderInfoDto> pagedOrderInfo = orderInfosResponse.getBody();
        log.info("회원{}의 주문목록:{}", userId, pagedOrderInfo.getContent());

        model.addAttribute("pagedOrderInfo", pagedOrderInfo);

        return "order/order-list";
    }

    @GetMapping("/mypage/orders/{order-id}")
    public String orderDetailPage(@PathVariable("order-id") Long orderId,
                                  @Auth String userId,
                                  Model model){
        String encryptedUserId = simpleEncryptor.encrypt(userId);
        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(orderId));

        ResponseEntity<OrderWithOrderDetailResponse> responseEntity = userOrderInfoClient.getOrderWithOrderDetails(encryptedUserId, encryptedOrderId);
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if(statusCode.is4xxClientError() || statusCode.is5xxServerError()){
            log.info("잘못된 접근입니다. statusCode={}, userId={}, orderId={}", statusCode, userId, orderId);
            return "redirect:/mypage/orders";
        }
        OrderWithOrderDetailResponse orderWithOrderDetailResponse = responseEntity.getBody();

        OrderRelatedInfoDto orderRelatedInfoDto = orderWithOrderDetailResponse.getOrderRelatedInfoDto();
        List<OrderDetailInfoDto> orderDetailInfoDtoList = orderWithOrderDetailResponse.getOrderDetailInfoDtoList();
        log.info("order info={}", orderRelatedInfoDto);
        log.info("order detail info={}", orderDetailInfoDtoList);

        model.addAttribute("orderInfo", orderRelatedInfoDto);
        model.addAttribute("orderDetails", orderDetailInfoDtoList);

        return "order/order-detail";
    }

    @PostMapping("/mypage/order/cancel")
    public String cancelOrder(@RequestParam("order-id") Long orderId,
                              @Auth String userId){

        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(orderId));
        String encryptedUserId = simpleEncryptor.encrypt(userId);

        ResponseEntity<Boolean> booleanResponseEntity = genericUserOrderClient.isOwner(encryptedOrderId, encryptedUserId, null);
        HttpStatusCode statusCode = booleanResponseEntity.getStatusCode();

        if(statusCode.is4xxClientError() || statusCode.is5xxServerError()){
            log.info("잘못된 접근입니다. statusCode={}, userId={}, orderId={}", statusCode, userId, orderId);
            return "redirect:/mypage/orders/" + orderId;
        }

        Boolean isOwner = booleanResponseEntity.getBody();
        if(!isOwner){
            log.info("주문 취소 권한이 없습니다. userId={}, orderId={}", userId, orderId);
            return "redirect:/mypage/orders/" + orderId;
        }

        //orderId로 order를 취소하는 api
        Long canceledOrderId = genericUserOrderClient.orderCancel(encryptedOrderId).getBody();

        return "redirect:/mypage/orders/" + canceledOrderId;
    }

    @PostMapping("/mypage/order/refund")
    public String refundOrder(@RequestParam("order-id") Long orderId,
                              @Auth String userId){

        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(orderId));
        String encryptedUserId = simpleEncryptor.encrypt(userId);

        ResponseEntity<Boolean> booleanResponseEntity = genericUserOrderClient.isOwner(encryptedOrderId, encryptedUserId, null);
        HttpStatusCode statusCode = booleanResponseEntity.getStatusCode();

        if(statusCode.is4xxClientError() || statusCode.is5xxServerError()){
            log.info("잘못된 접근입니다. statusCode={}, userId={}, orderId={}", statusCode, userId, orderId);
            return "redirect:/mypage/orders/" + orderId;
        }

        Boolean isOwner = booleanResponseEntity.getBody();
        if(!isOwner){
            log.info("주문 취소 권한이 없습니다. userId={}, orderId={}", userId, orderId);
            return "redirect:/mypage/orders/" + orderId;
        }

        //orderId로 order를 환불하는 api
        Long refundOrderId = genericUserOrderClient.requestRefund(encryptedOrderId).getBody();

        return "redirect:/mypage/orders/" + refundOrderId;
    }
}
