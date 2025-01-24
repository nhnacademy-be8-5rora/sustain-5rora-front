package store.aurora.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.aurora.coupon.dto.CouponState;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class RequestUserCouponDTO {
    @NotNull private List<String> userIds;    // 유저 ID 리스트
    @NotNull private Long couponPolicyId; // 정책 ID
    private CouponState state;   // 쿠폰 상태
    private LocalDate startDate; // 시작일
    private LocalDate endDate;   // 종료일
}

