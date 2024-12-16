package store.aurora.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoInfoResponse(
        @JsonProperty("header") PaycoInfoHeader header,
        @JsonProperty("data") PaycoInfoData data
) {
}
