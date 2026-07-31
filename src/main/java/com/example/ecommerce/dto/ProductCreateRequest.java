package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 어드민 상품 등록 "요청" DTO.
public record ProductCreateRequest(

        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId,

        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        int price,

        @Min(value = 0, message = "초기 재고는 0개 이상이어야 합니다.")
        int stockQuantity,

        String description
) {
}
