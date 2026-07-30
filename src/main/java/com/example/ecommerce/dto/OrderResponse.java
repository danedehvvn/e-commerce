package com.example.ecommerce.dto;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

// 주문 "응답" DTO. 주문 항목 목록을 함께 담는다.
public record OrderResponse(
        Long orderId,
        OrderStatus status,
        int totalPrice,
        LocalDateTime orderedAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt(),
                items
        );
    }
}
