package store.aurora.order.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailDTO {
    private Long bookId;
    private Integer quantity;
    private Long wrapId;
    private Long couponId;
}
