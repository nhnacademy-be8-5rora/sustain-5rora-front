package store.aurora.order.dto;

import java.util.List;

public record AdminOrderDTO(
        Long orderId,
        String shipmentState,
        List<AdminOrderDetailDTO> orderDetailList
) {
}
