package com.example.ecommerce.global.exception;

// 인증은 됐지만 "그 자원에 대한 권한이 없을" 때 → 403.
//   (예: 남의 장바구니 항목/주문을 조작하려 할 때)
// 역할(ADMIN) 기반 인가는 SecurityConfig가 막지만, "본인 소유인지" 같은
//   데이터 단위 검증은 서비스 로직에서 해야 하므로 이 예외로 표현한다.
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
