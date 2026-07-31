package com.example.ecommerce.dto;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderStatus;
import java.time.LocalDateTime;

// 주문 "목록용" 요약 DTO. 상세와 달리 주문항목(items)을 담지 않는다.
//   → 목록에서 주문마다 항목/상품을 조회하는 N+1을 피하고, 응답도 가볍다.
//   (항목이 궁금하면 상세 조회 GET /api/orders/{id}로)
public record OrderSummaryResponse(
        Long orderId,
        Long memberId,
        OrderStatus status,
        int totalPrice,
        LocalDateTime orderedAt
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getMember().getId(), // LAZY지만 id는 프록시에서 바로 얻어 추가 쿼리 없음
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt()
        );
    }
}
