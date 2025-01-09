package store.aurora.coupon.dto.request;

import lombok.Data;

@Data
public class DiscountRuleDTO {
    private Integer needCost;
    private Integer maxSale;
    private Integer salePercent;
    private Integer saleAmount;
}