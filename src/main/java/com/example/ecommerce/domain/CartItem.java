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

// 장바구니 "한 줄" = 어떤 회원이, 어떤 상품을, 몇 개 담았는가.
// (장바구니 전체를 담는 Cart 엔티티는 두지 않고, 회원별 CartItem 목록으로 단순화)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누구의 장바구니인가. 여러 CartItem → 한 Member (다대일), 지연 로딩.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떤 상품인가. 여러 CartItem → 한 Product (다대일), 지연 로딩.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private CartItem(Member member, Product product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public static CartItem create(Member member, Product product, int quantity) {
        return CartItem.builder()
                .member(member)
                .product(product)
                .quantity(quantity)
                .build();
    }

    // 이미 담긴 상품을 또 담으면 수량을 더한다.
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    // 장바구니에서 수량을 직접 바꿀 때. 1개 미만은 허용하지 않는다.
    public void changeQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }
}
