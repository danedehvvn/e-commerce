package com.example.ecommerce.global.exception;

// "상품을 찾을 수 없음"을 나타내는 비즈니스 예외.
//
// RuntimeException(언체크 예외)을 상속하는 이유:
//   - CheckedException으로 만들면 서비스마다 throws를 줄줄이 달거나 try/catch로 감싸야 해서 코드가 지저분해진다.
//   - 스프링 트랜잭션은 기본적으로 RuntimeException이 터질 때 자동 롤백된다.
// 그래서 스프링에서 비즈니스 예외는 관례적으로 RuntimeException 계열로 만든다.
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long productId) {
        // 이 메시지가 나중에 전역 예외 처리기에서 응답 message로 쓰인다.
        super("상품을 찾을 수 없습니다. id=" + productId);
    }
}
