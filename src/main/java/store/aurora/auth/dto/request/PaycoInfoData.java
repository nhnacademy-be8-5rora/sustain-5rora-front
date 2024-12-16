package store.aurora.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoInfoData(
    @JsonProperty("member") PaycoInfoMember member
) {}
