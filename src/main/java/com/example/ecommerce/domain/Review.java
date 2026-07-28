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

// 상품 리뷰 = 어떤 회원이, 어떤 상품에, 별점과 내용을 남긴다.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자. 지연 로딩 단방향.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 리뷰 대상 상품. 지연 로딩 단방향.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 별점 (1~5)
    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String content;

    @Builder
    private Review(Member member, Product product, int rating, String content) {
        this.member = member;
        this.product = product;
        this.rating = rating;
        this.content = content;
    }

    public static Review create(Member member, Product product, int rating, String content) {
        validateRating(rating);
        return Review.builder()
                .member(member)
                .product(product)
                .rating(rating)
                .content(content)
                .build();
    }

    // 별점은 1~5 범위만 허용
    private static void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지만 가능합니다.");
        }
    }
}
