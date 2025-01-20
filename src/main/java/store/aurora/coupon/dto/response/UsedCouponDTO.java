package store.aurora.coupon.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsedCouponDTO {
    private String couponName;  //쿠폰 이름
    private LocalDate usedDate; //쿠폰 사용한 날짜
    private LocalDate startDate;
    private LocalDate endDate;
}
