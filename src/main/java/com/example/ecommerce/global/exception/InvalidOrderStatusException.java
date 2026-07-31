package com.example.ecommerce.global.exception;

import com.example.ecommerce.domain.OrderStatus;

// 허용되지 않은 주문 상태 전이를 시도했을 때 → 400.
//   예: 배송중(SHIPPING)인 주문을 취소(CANCELED)하려 할 때.
public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(OrderStatus from, OrderStatus to) {
        super("허용되지 않은 주문 상태 전이입니다. " + from + " → " + to);
    }
}
