package store.aurora.order.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInfoDto {
    private Long orderId;
    private Integer totalAmount;
    private String orderState;
    private LocalDateTime orderTime;
    private String orderContent;
}
