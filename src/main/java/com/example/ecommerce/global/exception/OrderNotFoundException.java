package com.example.ecommerce.global.exception;

// 주문을 찾을 수 없음 → 404.
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("주문을 찾을 수 없습니다. id=" + orderId);
    }
}
