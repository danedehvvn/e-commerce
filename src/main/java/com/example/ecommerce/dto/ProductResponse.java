package com.example.ecommerce.dto;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductStatus;

// 상품 "응답 전용" DTO.
// 엔티티(Product)를 그대로 노출하지 않고, 클라이언트에게 보여줄 필드만 골라 담는다.
public record ProductResponse(
        Long id,
        String name,
        int price,
        int stockQuantity,
        String description,
        ProductStatus status,
        Long categoryId,     // 연관 엔티티(Category)는 통째로 넣지 않고 필요한 값만 평탄화
        String categoryName
) {
    // 엔티티 → DTO 변환.
    // ※ product.getCategory()는 LAZY라, 이 변환은 반드시 "트랜잭션 안(Service)"에서 호출해야
    //    실제 카테고리 값을 가져올 수 있다. (트랜잭션 밖이면 LazyInitializationException)
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getStatus(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
