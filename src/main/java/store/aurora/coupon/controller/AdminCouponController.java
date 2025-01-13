package store.aurora.coupon.controller;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.coupon.dto.request.*;
import store.aurora.feign_client.coupon.CouponClient;

//관리자용 쿠폰 생성 및 배포용
//관리자를 통해서 새 쿠폰을 데이터베이스에 저장하기 때문에 유효성, 무결성 중요(@Validated 사용)
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController {

    private final CouponClient couponClient;
    private static final String COUPON_ADMIN_URL = "redirect:/admin/coupon/create";
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    // 쿠폰 생성 (관리자 창)으로 이동 -> 쿠폰 정책은 생성밖에 안됨(정책 수정, 삭제시 -> 이전에 해당 쿠폰을 가진 유저들이 피해를 볼 수 있기에)
    @GetMapping(value = "/coupon/create")
    public String goAdminCoupon() {

        return "admin/coupon/coupon-create";
    }

    // 쿠폰 정책 생성
    @PostMapping(value = "/policy/create")
    public String couponPolicyCreate(@ModelAttribute @Validated RequestCouponPolicyDTO requestCouponPolicyDTO) {
        USER_LOG.info("requestCouponPolicyDTO: {}", requestCouponPolicyDTO);
        return executeWithLogging(() -> couponClient.couponPolicyCreate(requestCouponPolicyDTO));
    }

    // 사용자 쿠폰 생성
    @PostMapping("/coupon/distribution")
    public String userCouponCreate(@ModelAttribute @Validated RequestUserCouponDTO requestUserCouponDTO) {
        return executeWithLogging(() -> couponClient.userCouponCreate(requestUserCouponDTO));
    }

    // 사용자 쿠폰 수정
    @PutMapping(value = "/coupon/update")
    public String userCouponUpdate(@ModelAttribute @Validated UpdateUserCouponDto updateUserCouponDto) {
        return executeWithLogging(() -> couponClient.couponUpdate(updateUserCouponDto));
    }

    // 공통 처리 메서드
    private String executeWithLogging(Runnable clientAction) {
        try {
            clientAction.run();
            return COUPON_ADMIN_URL;  // 성공 시 리다이렉트
        } catch (Exception e) {
            USER_LOG.error("작업 중 오류 발생", e);
            return COUPON_ADMIN_URL + "?error=true";  // 실패 시 오류 처리
        }
    }
}