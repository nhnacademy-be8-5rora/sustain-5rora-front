package store.aurora.order.dto;

public record PaymentRequest(String paymentKey, Integer amount, String orderId) { }
