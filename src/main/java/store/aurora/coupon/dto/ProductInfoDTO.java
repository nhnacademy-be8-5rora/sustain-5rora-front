package store.aurora.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProductInfoDTO {
    @NotNull
    private Long productId;   //결제 상품 목록

    @NotNull
    private List<Long> categoryIds;  //상품이 속해있는 카테고리 Id
    @NotNull
    private Long bookId; //상품의 고유 ID
    @NotNull
    private Integer price; //상품의 가격

    public ProductInfoDTO(Long productId, List<Long> categoryIds, Long bookId, Integer price) {
        this.productId = productId;
        this.categoryIds = categoryIds;
        this.bookId = bookId;
        this.price = price;
    }

    public ProductInfoDTO() {

    }
}
