package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// 어드민 상품 정보 수정 "요청" DTO. (재고/상태는 별도 엔드포인트에서 다룬다)
public record ProductUpdateRequest(

        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        int price,

        String description
) {
}
