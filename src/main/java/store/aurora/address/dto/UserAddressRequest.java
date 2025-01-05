package store.aurora.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAddressRequest {
    @NotBlank
    private String receiver;
    @NotBlank
    private String roadAddress;
    @NotBlank
    private String addrDetail;
}