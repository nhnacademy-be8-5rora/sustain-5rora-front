package store.aurora.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.coupon.dto.request.RequestCouponPolicyDTO;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.coupon.dto.request.UpdateUserCouponByUserIdDto;
import store.aurora.feignClient.coupon.CouponClient;

//관리자용 쿠폰 생성 및 배포용
//관리자를 통해서 새 쿠폰을 데이터베이스에 저장하기 때문에 유효성, 무결성 중요(@Validated 사용)
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponClient couponClient;

    // 쿠폰 정책 추가
    @PostMapping("/create")
    public String createCouponPolicyForm(@RequestBody @Validated RequestCouponPolicyDTO dto, Model model) {
        couponClient.couponPolicyCreate(dto);
        model.addAttribute("coupon", dto);
        return "home/home";
    }

    //사용자 쿠폰 생성(특정 한명에게 줄 수 있으며, 특정 조건을 충족한 유저들에게 쿠폰을 뿌릴 수 있도록 함)
    @PostMapping("/coupon/distribution")
    public ResponseEntity<String> userCouponCreate(@RequestBody @Validated
                                                   RequestUserCouponDTO requestUserCouponDTO) {

        couponClient.userCouponCreate(requestUserCouponDTO);

        return ResponseEntity.ok("사용자쿠폰이 생성되었습니다.");
    }

    // 사용자쿠폰 수정 (관리자)
    @PutMapping(value = "/coupon/update/")
    public ResponseEntity<String> userCouponUpdate(@RequestBody @Validated
                                                   UpdateUserCouponByUserIdDto updateUserCouponByUserIdDto) {

        couponClient.couponUpdate(updateUserCouponByUserIdDto);  // 실제 쿠폰 수정 처리
        return ResponseEntity.ok("사용자쿠폰이 수정되었습니다.");
    }

    //모든 사용자 쿠폰을 확인해서 해당 쿠폰 정책 ID가 있는지 파악한 후에 삭제, 수정 가능하도록 구현은 가능
}
