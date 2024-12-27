package store.aurora.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.coupon.dto.RequestUserCouponDTO;
import store.aurora.feignClient.coupon.CouponClient;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class WelcomeCouponController {

    private final CouponClient couponClient;

    // 회원가입 API
    @PostMapping("/welcomeCoupon")
    public String registerUser(@RequestBody Long userId) {

        // 사용자가 이미 Welcome 쿠폰을 보유하고 있는지 확인
        boolean alreadyHasCoupon = couponClient.existWelcomeCoupon(userId, 1L);

        if (alreadyHasCoupon) {
            return "이미 Welcome 쿠폰이 발급되었습니다.";
        }

        LocalDate currentDate = LocalDate.now();
        //WelcomeCoupon 생성(5만원 이상 구매시 1만 할인 쿠폰, 기간 30일)
        RequestUserCouponDTO requestUserCouponDTO = new RequestUserCouponDTO();
        requestUserCouponDTO.setUserId(List.of(userId));
        requestUserCouponDTO.setCouponPolicyId(1L); //사용자 환영 쿠폰 정책
        requestUserCouponDTO.setStartDate(currentDate);
        requestUserCouponDTO.setStartDate(currentDate.plusDays(30));

        boolean success = couponClient.userCouponCreate(requestUserCouponDTO);

        if (success) {
            return "회원가입 성공! Welcome 쿠폰 발급 요청이 처리되었습니다.";
        }

        return "Welcome 쿠폰 발급 요청이 실패되었습니다. 재발급 버튼을 눌러주세요.";
    }
}
