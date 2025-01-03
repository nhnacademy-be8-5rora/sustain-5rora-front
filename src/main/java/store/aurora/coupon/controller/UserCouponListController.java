package store.aurora.coupon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.JwtUtil;
import store.aurora.coupon.dto.response.ProductInfoDTO;
import store.aurora.coupon.dto.response.UserCouponDTO;
import store.aurora.feignClient.coupon.CouponClient;


import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserCouponListController {

    private final CouponClient couponClient;

    @GetMapping(value = "/coupon/list")
    public String couponList(HttpServletRequest request,
                             Model model) {

        String jwt = JwtUtil.getJwtFromCookie(request);

        List<UserCouponDTO> userCouponList = couponClient.getCouponList(jwt);

        model.addAttribute("userCouponList", userCouponList);

        return "/coupon/couponList";
    }

    //결제창에서 상품마다 사용가능 쿠폰 리스트 확인(매 상품마다 사용 가능한 쿠폰이 뜨게 해야 됨.
    @PostMapping(value = "/calculate")
    public ResponseEntity<Map<Long, List<String>>>proCouponList(HttpServletRequest request,
                                                          @RequestBody @Validated List<ProductInfoDTO> productInfoDTO) {   //결제 API에서 필요한 값을 받아야함

        String jwt = JwtUtil.getJwtFromCookie(request);

        //orderId에 있는 카테고리, 북 ID을 불러와서 해당 사용자 쿠폰의 쿠폰정책과 비교해서 쓸 있는지 없는지 확인
        Map<Long, List<String>> userCoupons = couponClient.getCouponListByCategory(productInfoDTO, jwt);

        return ResponseEntity.ok(userCoupons);
    }
}