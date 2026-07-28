package com.example.ecommerce.domain;

// 상품의 판매 상태.
public enum ProductStatus {
    ON_SALE,       // 판매중
    SOLD_OUT,      // 품절 (재고 0)
    DISCONTINUED   // 판매중지 (운영자가 내림)
}
