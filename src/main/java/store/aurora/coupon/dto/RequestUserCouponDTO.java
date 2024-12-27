package store.aurora.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestUserCouponDTO {
    @NotNull
    private List<Long> userId;    // 유저 ID 리스트
    @NotNull
    private Long couponPolicyId; // 정책 ID

    private CouponState state;   // 쿠폰 상태
    private LocalDate startDate; // 시작일
    private LocalDate endDate;   // 종료일
}

