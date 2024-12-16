package store.aurora.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoAccessTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("state") String state,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("access_token_secret") String accessTokenSecret
) {}
