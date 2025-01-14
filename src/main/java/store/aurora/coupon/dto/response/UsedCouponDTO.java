package store.aurora.coupon.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsedCouponDTO {
    private String couponName;
    private LocalDate usedDate; //쿠폰 사용한 날짜
    private long saleAmount;
}
