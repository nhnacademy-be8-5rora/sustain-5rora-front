package store.aurora.coupon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.common.JwtUtil;
import store.aurora.feignClient.coupon.CouponClient;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WelcomeCouponController {

    private static final String ALERT_MESSAGE = "alertMessage";
    private static final String HOME_PAGE = "home/home";

    private final CouponClient couponClient;

    //회원가입 또는 쿠폰 재발급 버튼시에 호출
    @GetMapping("/welcomeCoupon")
    public String registerUser(HttpServletRequest request, Model model) {

        String jwt = JwtUtil.getJwtFromCookie(request);

        String message = couponClient.existWelcomeCoupon(jwt, 1L);  //쿠폰정책(1L) -> 신규가입 쿠폰 정책
        model.addAttribute(ALERT_MESSAGE, message); //쿠폰 유무 파악후에 메시지전달

        return HOME_PAGE;   //홈페이지로 이동
    }
}