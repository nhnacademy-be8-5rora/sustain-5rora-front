package store.aurora.common.exception;

public abstract class FeignClientResolveFailException extends RuntimeException{
    public FeignClientResolveFailException() {
    }

    public FeignClientResolveFailException(String message) {
        super(message);
    }

    public FeignClientResolveFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeignClientResolveFailException(Throwable cause) {
        super(cause);
    }
}
