package store.aurora.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyRequest {
    @NotNull @Min(1) private Integer pointPolicyId;
    private String pointPolicyName;
    private String pointPolicyType; // Enum 타입 (AMOUNT, PERCENTAGE)
    private BigDecimal pointPolicyValue;
}