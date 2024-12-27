package store.aurora.coupon.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;


//사용자에게 필요한 쿠폰 정보
@Data
public class UserCouponDTO {

    private Long couponId;       // 쿠폰 ID
    private Long needCost;
    private Long maxSale;
    private Long salePercent;
    private Long saleAmount;
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate;   // 종료 날짜
    private List<Long> bookIdList;  //쿠폰 사용 가능한 책 ID
    private List<Long> categoryIdList; //쿠폰 사용 가능한 카테고리 ID

}
