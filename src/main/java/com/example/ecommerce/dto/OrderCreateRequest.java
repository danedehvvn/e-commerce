package com.example.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// 주문 생성 "요청" DTO. 여러 상품을 한 번에 주문할 수 있다.
public record OrderCreateRequest(

        // @NotEmpty : 최소 1개 항목은 있어야 함.
        // @Valid : 리스트 안의 각 OrderItemRequest도 검증하게 한다(중첩 검증).
        @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderItemRequest> items
) {
}
