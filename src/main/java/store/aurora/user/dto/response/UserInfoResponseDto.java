package store.aurora.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class UserInfoResponseDto {
    // id, 이름, 생년월일, 전화번호, 이메일, 등급
    private String id;
    private String name;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String rankName;
}
