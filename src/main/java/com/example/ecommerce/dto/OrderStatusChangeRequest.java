package com.example.ecommerce.dto;

import com.example.ecommerce.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

// 어드민 주문 상태 변경 "요청" DTO.
public record OrderStatusChangeRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        OrderStatus status
) {
}
