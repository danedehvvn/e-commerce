package com.example.ecommerce.dto;

import com.example.ecommerce.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;

// 상품 판매상태 변경 "요청" DTO. (ON_SALE / SOLD_OUT / DISCONTINUED)
public record ProductStatusChangeRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        ProductStatus status
) {
}
