package store.aurora.order.dto;

public record AdminOrderDetailDTO(
        Long orderDetailId,
        String shipmentState,
        String shipmentDate
) {
}
