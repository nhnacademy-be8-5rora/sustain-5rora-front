package store.aurora.point.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PointPolicyUpdateRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal pointPolicyValue;
}