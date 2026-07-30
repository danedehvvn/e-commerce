package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 장바구니 담기 "요청" DTO.
public record CartItemAddRequest(

        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity
) {
}
