package store.aurora.order.exception;

public class CartEmptyException extends RuntimeException{
    public CartEmptyException() {
        super("cart item is null");
    }
}
