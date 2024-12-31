package store.aurora.coupon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.common.JwtUtil;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.feignClient.coupon.CouponClient;


import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WelcomeCouponController {

    private static final String ALERT_MESSAGE = "alertMessage";
    private static final String HOME_PAGE = "home/home";

    private final CouponClient couponClient;

    @GetMapping("/welcomeCoupon")
    public String registerUser(HttpServletRequest request, Model model) {

        String jwt = JwtUtil.getJwtFromCookie(request);

        String message = checkAndCreateWelcomeCoupon(jwt);
        model.addAttribute(ALERT_MESSAGE, message);
        return HOME_PAGE;
    }

    private String checkAndCreateWelcomeCoupon(String userId) {
        // 사용자가 이미 Welcome 쿠폰을 보유하고 있는지 확인
        if (couponClient.existWelcomeCoupon(userId, 1L)) {
            return "이미 Welcome 쿠폰이 발급되었습니다.";
        }

        // WelcomeCoupon 생성 로직
        RequestUserCouponDTO requestUserCouponDTO = createWelcomeCouponRequest(userId);

        boolean success = couponClient.userCouponCreate(requestUserCouponDTO);
        return success
                ? "회원가입 성공! Welcome 쿠폰 발급 요청이 처리되었습니다."
                : "Welcome 쿠폰 발급 요청이 실패되었습니다. 재발급 버튼을 눌러주세요.";
    }

    private RequestUserCouponDTO createWelcomeCouponRequest(String userId) {
        LocalDate currentDate = LocalDate.now();
        return RequestUserCouponDTO.builder()
                .userId(List.of(userId))
                .couponPolicyId(1L) // 사용자 환영 쿠폰 정책
                .startDate(currentDate)
                .endDate(currentDate.plusDays(30))
                .build();
    }
}