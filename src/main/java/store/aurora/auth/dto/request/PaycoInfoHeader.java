package store.aurora.auth.dto.request;

public record PaycoInfoHeader(
        boolean isSuccessful,
        int resultCode,
        String resultMessage
) { }
