package store.aurora.order.dto;

import lombok.Getter;
import lombok.ToString;
import store.aurora.order.enums.OrderState;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
public class OrderRelatedInfoDto {
    private Long orderId;
    private LocalDate prefferedDeliveryDate;
    private Integer deliveryFee;
    private LocalDateTime orderTime;
    private Integer totalAmount;
    private Integer pointAmount;
    private OrderState orderState;

    private String orderPhone;
    private String orderEmail;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String customerRequest;

}
