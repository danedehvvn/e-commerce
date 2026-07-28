package com.example.ecommerce.domain;

import com.example.ecommerce.global.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문. 한 회원이 여러 OrderItem을 담아 한 번에 주문한다.
//
// @Table(name = "orders")
//   : ORDER는 SQL 예약어(ORDER BY)라, 테이블명을 order로 만들면 쿼리에서 충돌/문법오류가 난다.
//     그래서 테이블명을 orders로 바꿔 지정한다. (클래스명은 Order 그대로 두고 테이블명만 변경)
@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문한 회원. 여러 Order → 한 Member (다대일), 지연 로딩.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // ── 양방향 연관관계 (Order ↔ OrderItem) ──
    // mappedBy = "order" : "이 관계의 주인은 OrderItem의 order 필드다"라는 선언.
    //   → 즉 FK(order_id)는 OrderItem 쪽이 관리하고, Order의 이 리스트는 "읽기용 거울"이다.
    // cascade = ALL : Order를 저장/삭제하면 소속 OrderItem들도 함께 저장/삭제 (생명주기 공유).
    // orphanRemoval = true : 리스트에서 빠진 OrderItem은 DB에서도 삭제(고아 제거).
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    // ── 연관관계 편의 메서드 ──
    // 양방향은 양쪽을 다 세팅해줘야 데이터가 어긋나지 않는다.
    // "주문에 항목을 추가하면(리스트에 담고) + 그 항목의 order도 나로 지정"을 한 메서드로 묶는다.
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);   // 내(Order) 리스트에 담고
        orderItem.assignOrder(this);      // 그 항목의 order를 나로 연결
    }

    // 정적 팩토리 : 회원 + 주문항목들로 주문 생성.
    public static Order create(Member member, List<OrderItem> orderItems) {
        Order order = new Order();
        order.member = member;
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);   // 편의 메서드로 양방향 연결
        }
        order.status = OrderStatus.PAYMENT_WAITING; // 처음엔 결제대기
        order.orderedAt = LocalDateTime.now();
        order.totalPrice = order.calculateTotalPrice();
        return order;
    }

    // 전체 금액 = 각 항목 합계의 합
    private int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    // ── 상태 변경 (의미 있는 메서드) ──
    // 주문 취소 : 이미 배송이 시작됐으면 취소 불가. 취소되면 각 항목의 재고를 복구.
    public void cancel() {
        if (this.status == OrderStatus.SHIPPING || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELED;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); // 재고 원복
        }
    }

    // 어드민이 주문 상태를 다음 단계로 전이시킬 때 사용.
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
