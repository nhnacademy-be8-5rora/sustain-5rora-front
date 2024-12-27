package store.aurora.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.coupon.dto.ProductInfoDTO;
import store.aurora.coupon.dto.UserCouponDTO;
import store.aurora.feignClient.coupon.CouponClient;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserCouponListController {

    private final CouponClient couponClient;


    @PostMapping(value = "/couponList/{userId}")
    public ResponseEntity<List<UserCouponDTO>> couponList(@PathVariable @Valid Long userId) {
        List<UserCouponDTO> userCouponList = couponClient.getCouponList(userId);

        return ResponseEntity.ok(userCouponList);
    }

    //결제창에서 상품마다 사용가능 쿠폰 리스트 확인(매 상품마다 사용 가능한 쿠폰이 뜨게 해야 됨.
    @PostMapping(value = "/calculate/{userId}")
    public ResponseEntity<Map<Long, List<String>>>proCouponList(@PathVariable Long userId,
                                                          @RequestBody @Validated List<ProductInfoDTO> productInfoDTO) {   //결제 API에서 필요한 값을 받아야함

        //orderId에 있는 카테고리, 북 ID을 불러와서 해당 사용자 쿠폰의 쿠폰정책과 비교해서 쓸 있는지 없는지 확인
        Map<Long, List<String>> userCoupons = couponClient.getCouponListByCategory(productInfoDTO, userId);

        return ResponseEntity.ok(userCoupons);
    }
}