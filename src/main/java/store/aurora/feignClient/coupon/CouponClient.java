package store.aurora.feignClient.coupon;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.coupon.dto.request.RequestCouponPolicyDTO;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.coupon.dto.request.UpdateUserCouponDto;
import store.aurora.coupon.dto.response.ProductInfoDTO;
import store.aurora.coupon.dto.response.UserCouponDTO;

import java.util.List;
import java.util.Map;


@FeignClient(name = "couponClient", url = "${api.gateway.base-url}" + "/api/coupon")
public interface CouponClient {

    // 쿠폰 정책 생성
    @PostMapping("/admin/create")
    ResponseEntity<String> couponPolicyCreate(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO);

    // 사용자 쿠폰 생성
    @PostMapping("/admin/distribution")
    Boolean userCouponCreate(@RequestBody RequestUserCouponDTO requestUserCouponDTO);

    // 사용자 쿠폰 수정
    @PutMapping("/admin/update")
    ResponseEntity<String> couponUpdate(@RequestBody UpdateUserCouponDto updateUserCouponDto);

    @PostMapping("/refund")
    void refund(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @PostMapping("/using")
    void used(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @PostMapping("/coupon/usable")
    Map<Long, List<String>> getCouponListByCategory(@RequestBody List<ProductInfoDTO> productInfoDTO,
                                                    @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @PostMapping("/coupon/list")
    List<UserCouponDTO> getCouponList(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @PostMapping("/welcome")
    String existWelcomeCoupon(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
                              @RequestBody Long couponPolicyId);

}
