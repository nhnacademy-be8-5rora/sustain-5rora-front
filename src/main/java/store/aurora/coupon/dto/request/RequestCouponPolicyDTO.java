package store.aurora.coupon.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCouponPolicyDTO {
    
    @NotNull
    private String policyName;
    @NotNull
    private SaleType saleType;

    private AddPolicyDTO addPolicyDTO;

    @NotNull
    private DiscountRuleDTO discountRuleDTO;
}

