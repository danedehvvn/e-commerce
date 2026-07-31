package com.example.ecommerce.global.exception;

// 카테고리를 찾을 수 없음 → 404.
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super("카테고리를 찾을 수 없습니다. id=" + categoryId);
    }
}
