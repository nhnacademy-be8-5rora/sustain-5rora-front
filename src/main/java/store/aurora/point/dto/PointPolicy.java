package store.aurora.point.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PointPolicy {
    private Integer id;
    private PointPolicyCategory pointPolicyCategory;
    private String pointPolicyName;
    private String pointPolicyType;
    private BigDecimal pointPolicyValue;
    private Boolean isActive;
}