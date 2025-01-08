package store.aurora.order.exception;

import store.aurora.common.exception.FeignClientResolveFailException;

public class BookClientResolveFailException extends FeignClientResolveFailException {
    public BookClientResolveFailException(Throwable cause) {
        super(cause);
    }
}
