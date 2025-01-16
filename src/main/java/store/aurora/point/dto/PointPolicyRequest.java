package store.aurora.point.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyRequest {
    @NotNull private PointPolicyCategory pointPolicyCategory;
    @NotNull private String pointPolicyName;
    @NotNull private String pointPolicyType; // api 서버에서 Enum 타입 (AMOUNT, PERCENTAGE) 으로 받음
    @NotNull private BigDecimal pointPolicyValue;
}