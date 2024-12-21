package store.aurora.config.security.exception.userInfoNotFound;

public abstract class Oauth2UserInfoNotFound extends RuntimeException{
    public Oauth2UserInfoNotFound() {
    }

    public Oauth2UserInfoNotFound(String message) {
        super(message);
    }

    public Oauth2UserInfoNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public Oauth2UserInfoNotFound(Throwable cause) {
        super(cause);
    }
}
