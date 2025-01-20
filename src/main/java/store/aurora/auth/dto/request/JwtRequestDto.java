package store.aurora.auth.dto.request;

public record JwtRequestDto(String username,
                            Long expiredMs){} // 다중 토큰으로 변환하기 전에 access token은 기존에 expiredMs 안 받았어서 호환되도록 nullable.
