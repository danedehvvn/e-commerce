package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;

// 재고 입고 "요청" DTO. quantity 만큼 재고를 늘린다.
public record StockAddRequest(

        @Min(value = 1, message = "입고 수량은 1개 이상이어야 합니다.")
        int quantity
) {
}
