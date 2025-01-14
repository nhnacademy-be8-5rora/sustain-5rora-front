package store.aurora.feign_client.coupon;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.coupon.dto.request.RequestCouponPolicyDTO;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.coupon.dto.request.UpdateUserCouponDto;
import store.aurora.coupon.dto.response.ProductInfoDTO;
import store.aurora.coupon.dto.response.UsedCouponDTO;
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
    ResponseEntity<String> userCouponCreate(@RequestBody RequestUserCouponDTO requestUserCouponDTO);

    // 사용자 쿠폰 수정
    @PostMapping("/admin/update")
    ResponseEntity<String> couponUpdate(@RequestBody UpdateUserCouponDto updateUserCouponDto);

    //환불시에 refund controller 작동(if문으로 해당 refund 상품하는 결제 내역에 쿠폰이 있다면 작동하게끔)
    @PostMapping("/refund")
    void refund(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    //쿠폰 사용시 결제 버튼에서 이것도 결제 내역에 쿠폰이 있다면 발동하게끔.
    @PostMapping("/using")
    void used(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    //사용가능한 쿠폰 리스트 출력
    @GetMapping("/usable")
    Map<Long, List<String>> getCouponListByCategory(@RequestBody List<ProductInfoDTO> productInfoDTO,
                                                    @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);
    //해당 유저의 쿠폰 리스트 출력
    @GetMapping("/list")
    List<UserCouponDTO> getCouponList(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    //회원가입 시에 welcome 쿠폰 증정 및 재발급
    @PostMapping("/welcome")
    String existWelcomeCoupon(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @GetMapping("/used/list")
    List<UsedCouponDTO> getUsedCouponList(String jwt);
}
