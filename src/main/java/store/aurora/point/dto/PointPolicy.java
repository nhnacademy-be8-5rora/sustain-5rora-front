package store.aurora.point.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PointPolicy {
    private Integer id;
    private String pointPolicyName;
    private String pointPolicyType;
    private BigDecimal pointPolicyValue;
}