package com.example.ecommerce.domain;

// 주문의 진행 상태. 어드민에서 이 상태를 순서대로 전이시킨다.
// (결제대기 → 결제완료 → 배송중 → 배송완료, 그리고 취소)
public enum OrderStatus {
    PAYMENT_WAITING, // 결제대기
    PAID,            // 결제완료
    SHIPPING,        // 배송중
    DELIVERED,       // 배송완료
    CANCELED         // 취소
}
