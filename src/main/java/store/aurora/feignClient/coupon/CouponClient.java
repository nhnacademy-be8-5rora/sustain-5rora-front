package store.aurora.feignClient.coupon;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/coupon/create")
    ResponseEntity<String> couponPolicyCreate(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO);

    // 사용자 쿠폰 생성
    @PostMapping("/coupon/distribution")
    Boolean userCouponCreate(@RequestBody RequestUserCouponDTO requestUserCouponDTO);

    // 사용자 쿠폰 수정
    @PutMapping("/coupon/update")
    ResponseEntity<String> couponUpdate(@RequestBody UpdateUserCouponByUserIdDto updateUserCouponByUserIdDto);

    boolean existWelcomeCoupon(Long userId, long l);

    void refund(@Valid List<Long> userCouponId);

    Map<Long, List<String>> getCouponListByCategory(List<ProductInfoDTO> productInfoDTO, Long userId);

    List<UserCouponDTO> getCouponList(@Valid Long userId);

    void used(@Valid List<Long> userCouponId);
}
