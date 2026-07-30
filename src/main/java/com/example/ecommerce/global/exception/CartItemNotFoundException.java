package com.example.ecommerce.global.exception;

// 장바구니 항목을 찾을 수 없음 → 404.
public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long cartItemId) {
        super("장바구니 항목을 찾을 수 없습니다. id=" + cartItemId);
    }
}
