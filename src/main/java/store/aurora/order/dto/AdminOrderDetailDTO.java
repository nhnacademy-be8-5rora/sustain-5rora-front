package store.aurora.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderDetailDTO{
    private String bookName;
    private Integer price;
    private Integer quantity;
}
