package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 주문 항목 "요청" 한 줄 = 어떤 상품을 몇 개.
public record OrderItemRequest(

        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity
) {
}
