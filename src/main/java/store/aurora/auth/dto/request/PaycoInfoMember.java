package store.aurora.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoInfoMember(
        @JsonProperty("idNo") String idNo,                // 회원 번호
        @JsonProperty("email") String email,               // 이메일 주소
        @JsonProperty("mobile") String mobile,              // 휴대폰 번호
        @JsonProperty("maskedEmail") String maskedEmail,         // 마스킹된 이메일 주소
        @JsonProperty("maskedMobile") String maskedMobile,        // 마스킹된 휴대폰 번호
        @JsonProperty("name") String name                // 이름
)
{ }
