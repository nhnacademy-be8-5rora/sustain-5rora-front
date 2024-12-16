package store.aurora.auth.dto.request;

public record PaycoInfoHeader(Boolean isSuccessful, int resultCode, String resultMessage) {
}
