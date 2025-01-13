package store.aurora.order.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderRequestDto {
    //인증정보
    private String username;
    private String nonMemberPassword;

    // 주문자 정보
    private String ordererName;
    private String ordererPhone;
    private String ordererEmail;

    // 받는 사람 정보
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String receiverAddress;
    private String receiverMessage;

    private int usedPoint;

    //상품 정보
    private List<OrderDetailDTO> orderDetailDTOList;

}
