package store.aurora.user.exception;

public class UserSignUpFailException extends RuntimeException{
    public UserSignUpFailException(String message) {
        super(message);
    }

    public UserSignUpFailException(Throwable cause) {
        super(cause);
    }
}
