package store.aurora.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyRequest {
    private String pointPolicyName;
    private String pointPolicyType; // Enum 타입 (AMOUNT, PERCENTAGE)
    private BigDecimal pointPolicyValue;
}