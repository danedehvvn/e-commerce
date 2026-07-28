package com.example.ecommerce.domain;

// 회원 권한. 일반 사용자와 운영자(어드민)를 구분한다.
// 나중에 Spring Security에서 "이 API는 ADMIN만" 같은 접근 제어에 쓰인다.
public enum Role {
    USER,   // 일반 회원
    ADMIN   // 운영자
}
