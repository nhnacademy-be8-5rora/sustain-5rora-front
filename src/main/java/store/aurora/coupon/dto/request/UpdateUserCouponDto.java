package store.aurora.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import store.aurora.coupon.dto.CouponState;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateUserCouponDto {
    @NotNull private List<Long> updateUserIds;    // 바꿀 유저쿠폰의 ID 리스트
    private Long updatePolicyId; // 바꿀 정책의 ID
    private CouponState updateState;   // 변경할 쿠폰 상태
    private LocalDate updateEndDate;   // 종료일
}
