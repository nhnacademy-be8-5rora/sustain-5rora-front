package store.aurora.config.security.exception.connectionFail;

public abstract class ConnectionFailException extends RuntimeException{
    public ConnectionFailException() {
    }

    public ConnectionFailException(String message) {
        super(message);
    }

    public ConnectionFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionFailException(Throwable cause) {
        super(cause);
    }
}
