package store.aurora.auth.dto.response;

public record PaycoAccessTokenResponse(String accessTokenSecret, String state, String tokenType, String expiresIn, String refreshToken, String accessToken) {
}
