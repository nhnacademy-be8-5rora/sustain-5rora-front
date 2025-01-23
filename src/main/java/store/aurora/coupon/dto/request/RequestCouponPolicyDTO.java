package store.aurora.coupon.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.aurora.coupon.dto.SaleType;

//정책 쿠폰 생성 설정
@Getter
@Setter
@Builder
public class RequestCouponPolicyDTO {
    @NotNull private String policyName;
    @NotNull private SaleType saleType;
    private AddPolicyDTO addPolicyDTO;
    @NotNull private DiscountRuleDTO discountRuleDTO;
}