package store.aurora.order.exception;

import store.aurora.common.exception.FeignClientResolveFailException;

public class OrderTemporaryStorageClientResolverFailException extends FeignClientResolveFailException {
    public OrderTemporaryStorageClientResolverFailException(Throwable cause) {
        super(cause);
    }
}
