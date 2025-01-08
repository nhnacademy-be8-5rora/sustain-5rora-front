package store.aurora.order.dto;

public record CheckoutBookDTO(Long bookId, String title, Integer price, Integer quantity) {
}
