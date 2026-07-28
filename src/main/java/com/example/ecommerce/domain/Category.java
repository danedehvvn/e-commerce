package com.example.ecommerce.domain;

import com.example.ecommerce.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상품 분류 (예: 문구, 주방, 욕실 ...). Product가 이걸 ManyToOne으로 참조한다.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder
    private Category(String name) {
        this.name = name;
    }

    // 정적 팩토리 : 카테고리 생성 통로
    public static Category create(String name) {
        return Category.builder()
                .name(name)
                .build();
    }
}
