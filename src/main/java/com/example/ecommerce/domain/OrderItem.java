package com.example.ecommerce.domain;

import com.example.ecommerce.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문 안의 "상품 한 줄" = 이 주문에서 어떤 상품을 몇 개, 얼마에 샀는가.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 항목이 속한 주문. 여러 OrderItem → 한 Order (다대일). 양방향의 "주인" 쪽.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 어떤 상품인가. 지연 로딩.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // ★ 주문 시점의 가격 "스냅샷" ★
    // Product.price를 그대로 참조하지 않고 값을 복사해 저장한다.
    // 나중에 상품 가격이 바뀌어도, 과거 주문 금액은 절대 변하면 안 되기 때문.
    // (영수증에 찍힌 금액이 나중에 바뀌면 큰일)
    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int count;

    @Builder
    private OrderItem(Product product, int orderPrice, int count) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    // 정적 팩토리 : 주문 항목을 만들면서 그 자리에서 재고를 차감한다.
    public static OrderItem create(Product product, int count) {
        product.decreaseStock(count);            // 재고 깎기
        return OrderItem.builder()
                .product(product)
                .orderPrice(product.getPrice())  // "지금" 가격을 복사해 스냅샷으로 고정
                .count(count)
                .build();
    }

    // 연관관계 편의 메서드가 호출해, 이 항목에 부모 주문을 연결한다.
    void assignOrder(Order order) {
        this.order = order;
    }

    // 이 항목의 합계 금액 (단가 스냅샷 × 수량)
    public int getTotalPrice() {
        return orderPrice * count;
    }

    // 주문 취소 시 : 깎았던 재고를 되돌린다.
    public void cancel() {
        product.increaseStock(count);
    }
}
