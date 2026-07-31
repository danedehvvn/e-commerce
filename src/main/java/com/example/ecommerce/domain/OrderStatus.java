package com.example.ecommerce.domain;

import java.util.Set;

// 주문의 진행 상태. 어드민에서 이 상태를 순서대로 전이시킨다.
// (결제대기 → 결제완료 → 배송중 → 배송완료, 그리고 취소)
public enum OrderStatus {
    PAYMENT_WAITING, // 결제대기
    PAID,            // 결제완료
    SHIPPING,        // 배송중
    DELIVERED,       // 배송완료
    CANCELED;        // 취소

    // ── 상태 전이 규칙 ──
    // "이 상태에서 next 상태로 바꿀 수 있는가?"를 판단한다.
    //   전이 규칙을 도메인(enum) 안에 두는 이유: 규칙이 여러 서비스에 흩어지지 않고
    //   "상태에 관한 지식"이 상태 그 자신(enum)에 응집되어, 한곳만 보면 전이 규칙을 알 수 있다.
    //
    // switch 표현식은 enum 전체를 빠짐없이 다뤄야 컴파일되므로(누락 방지),
    //   나중에 상태를 추가하면 여기서 컴파일 에러로 "규칙도 갱신하라"고 알려준다.
    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PAYMENT_WAITING -> next == PAID || next == CANCELED;
            case PAID -> next == SHIPPING || next == CANCELED;
            case SHIPPING -> next == DELIVERED;      // 배송 시작 후엔 취소 불가
            case DELIVERED, CANCELED -> false;        // 종료 상태 → 어디로도 못 감
        };
    }

    // 현재 상태에서 갈 수 있는 상태 목록(문서/응답용).
    public Set<OrderStatus> nextStatuses() {
        return switch (this) {
            case PAYMENT_WAITING -> Set.of(PAID, CANCELED);
            case PAID -> Set.of(SHIPPING, CANCELED);
            case SHIPPING -> Set.of(DELIVERED);
            case DELIVERED, CANCELED -> Set.of();
        };
    }
}
