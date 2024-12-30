package store.aurora.feignClient.coupon;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.coupon.dto.request.RequestCouponPolicyDTO;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.coupon.dto.request.UpdateUserCouponByUserIdDto;
import store.aurora.coupon.dto.response.ProductInfoDTO;
import store.aurora.coupon.dto.response.UserCouponDTO;

import java.util.List;
import java.util.Map;


@FeignClient(name = "couponClient", url = "${api.gateway.base-url}" + "/api/coupon")
public interface CouponClient {

    // 쿠폰 정책 생성
    @PostMapping("/create")
    ResponseEntity<String> couponPolicyCreate(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO);

    // 사용자 쿠폰 생성
    @PostMapping("/distribution")
    Boolean userCouponCreate(@RequestBody RequestUserCouponDTO requestUserCouponDTO);

    // 사용자 쿠폰 수정
    @PutMapping("/update")
    ResponseEntity<String> couponUpdate(@RequestBody UpdateUserCouponByUserIdDto updateUserCouponByUserIdDto);

    @PostMapping("/{userId}")
    boolean existWelcomeCoupon(@PathVariable String userId, @RequestBody Long couponPolicyId );

    @PostMapping
    void refund(@Valid List<Long> userCouponId);

    @GetMapping
    Map<Long, List<String>> getCouponListByCategory(@RequestBody List<ProductInfoDTO> productInfoDTO, Long userId);

    @GetMapping("/user/{userId}")
    List<UserCouponDTO> getCouponList(@PathVariable String userId);

    @PostMapping
    void used(@Valid List<Long> userCouponId);
}
