package com.example.ecommerce.domain;

import com.example.ecommerce.global.BaseTimeEntity;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 판매 상품. 하나의 Category에 속한다(다대일).
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── 연관관계: 여러 Product → 하나의 Category (다대일) ──
    // @ManyToOne(LAZY) : 반드시 지연 로딩. Product를 조회할 때 Category를 즉시 가져오지 않고,
    //   실제로 product.getCategory()를 쓰는 순간에만 별도 쿼리로 가져온다. (EAGER 금지 이유는 설명 참고)
    // @JoinColumn(name = "category_id") : product 테이블에 만들어질 외래키(FK) 컬럼 이름.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    // 가격(원). 소수점이 없는 원화라 int로 충분.
    @Column(nullable = false)
    private int price;

    // 재고 수량. 나중에 "주문 시 재고 차감 동시성 제어"의 대상이 되는 핵심 필드.
    @Column(nullable = false)
    private int stockQuantity;

    // 상품 설명. 길 수 있으니 길이를 넉넉히.
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Builder
    private Product(Category category, String name, int price, int stockQuantity, String description) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        // 생성 시 기본 상태는 판매중
        this.status = ProductStatus.ON_SALE;
    }

    public static Product create(Category category, String name, int price, int stockQuantity, String description) {
        return Product.builder()
                .category(category)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .description(description)
                .build();
    }

    // ── 상태 변경은 "의미 있는 메서드"로만 (setter 대신) ──
    // 재고 차감. 부족하면 예외를 던져 잘못된 주문을 막는다.
    // (동시성 제어는 다음 단계에서 이 메서드를 기반으로 다룬다)
    public void decreaseStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.stockQuantity);
        }
        this.stockQuantity = restStock;
        if (this.stockQuantity == 0) {
            this.status = ProductStatus.SOLD_OUT; // 재고 0이면 품절 처리
        }
    }

    // 주문 취소 등으로 재고를 다시 늘린다.
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
        if (this.status == ProductStatus.SOLD_OUT && this.stockQuantity > 0) {
            this.status = ProductStatus.ON_SALE; // 품절이었다면 다시 판매중
        }
    }
}
