package store.aurora.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateShipmentStatusRequest {
    private Long orderId;
    private String shipmentState;
}
