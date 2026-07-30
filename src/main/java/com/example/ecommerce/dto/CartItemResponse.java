package com.example.ecommerce.dto;

import com.example.ecommerce.domain.CartItem;

// 장바구니 항목 "응답" DTO.
public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        int price,        // 상품의 현재 단가
        int quantity,
        int totalPrice     // 단가 × 수량
) {
    public static CartItemResponse from(CartItem cartItem) {
        int price = cartItem.getProduct().getPrice();
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                price,
                cartItem.getQuantity(),
                price * cartItem.getQuantity()
        );
    }
}
