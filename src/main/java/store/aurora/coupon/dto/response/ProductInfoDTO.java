package store.aurora.coupon.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductInfoDTO {
    @NotNull private Long productId;   //결제 상품 목록
    @NotNull private List<Long> categoryIds;  //상품이 속해있는 카테고리 Id
    @NotNull private Long bookId; //상품의 고유 ID
    @NotNull private Integer price; //상품의 가격
}
