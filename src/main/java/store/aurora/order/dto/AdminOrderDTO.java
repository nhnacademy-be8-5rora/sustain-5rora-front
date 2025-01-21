package store.aurora.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminOrderDTO{
    private Long orderId;
    private String shipmentState;
    private String shipmentDate;
    private String preferShipmentDate;
    private List<AdminOrderDetailDTO> orderDetailList;
}
