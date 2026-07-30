package com.example.ecommerce.dto;

import com.example.ecommerce.domain.OrderItem;

// 주문 항목 "응답" DTO.
public record OrderItemResponse(
        Long productId,
        String productName,
        int orderPrice,    // 주문 시점의 단가 스냅샷 (현재 상품가와 다를 수 있음)
        int count,
        int totalPrice      // orderPrice × count
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getOrderPrice(),
                orderItem.getCount(),
                orderItem.getTotalPrice()
        );
    }
}
