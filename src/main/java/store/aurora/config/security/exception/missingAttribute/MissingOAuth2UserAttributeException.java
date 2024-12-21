package store.aurora.config.security.exception.missingAttribute;

public abstract class MissingOAuth2UserAttributeException extends RuntimeException{
    public MissingOAuth2UserAttributeException() {
    }

    public MissingOAuth2UserAttributeException(String message) {
        super(message);
    }

    public MissingOAuth2UserAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingOAuth2UserAttributeException(Throwable cause) {
        super(cause);
    }
}
