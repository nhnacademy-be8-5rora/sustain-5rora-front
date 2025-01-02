package store.aurora.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAddressDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String receiver;
    @NotBlank
    private String roadAddress;
    @NotBlank
    private String addrDetail;
}