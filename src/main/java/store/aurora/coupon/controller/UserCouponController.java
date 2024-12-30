package store.aurora.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.feignClient.coupon.CouponClient;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserCouponController {

    private final CouponClient couponClient;

    //사용자 쿠폰 환불시 해당 사용자 쿠폰 상태 변경 및 데이터베이스 동기화
    @PutMapping(value = "/refund")
    public ResponseEntity<String> userCouponRefund(@RequestBody @Valid List<String> userCouponId){

        couponClient.refund(userCouponId);

        return ResponseEntity.ok("User Coupon refunded successfully.");
    }

    //사용자 쿠폰 사용시 해당 사용자 쿠폰 상태 변경 및 데이터베이스 동기화
    @PutMapping(value = "/using/")
    public ResponseEntity<String> userCouponUsing(@RequestBody @Valid List<String> userCouponId){

        couponClient.used(userCouponId);

        return ResponseEntity.ok("User Coupon used successfully.");
    }
}
