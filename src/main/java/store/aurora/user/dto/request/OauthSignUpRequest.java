package store.aurora.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//Oauth 회원가입 전용
//todo 삭제
public record OauthSignUpRequest(
        @NotBlank(message = "아이디는 필수 항목입니다.")
        String id,

        @NotBlank(message = "이름은 필수 항목입니다.")
        String name,

        @NotBlank(message = "생년월일은 필수 항목입니다.")
        String birth,

        @NotBlank(message = "전화번호는 필수 항목입니다.")
        String phoneNumber,

        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "올바른 이메일 형식을 입력하세요.")
        String email
) { }
