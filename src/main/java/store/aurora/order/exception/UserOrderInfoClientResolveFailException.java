package store.aurora.order.exception;

import store.aurora.common.exception.FeignClientResolveFailException;

public class UserOrderInfoClientResolveFailException extends FeignClientResolveFailException {
    public UserOrderInfoClientResolveFailException(Throwable cause) {
        super(cause);
    }
}
