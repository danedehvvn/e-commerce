package com.example.ecommerce.global.exception;

import com.example.ecommerce.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
//   : 모든 컨트롤러에서 발생한 예외를 "한곳에서" 가로채 처리하는 전역 핸들러.
//     (@ControllerAdvice + @ResponseBody → 반환값이 JSON으로 나간다)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(X.class)
    //   : X 예외가 터지면 이 메서드가 실행된다.
    // ProductNotFoundException → 404 Not Found 로 변환.
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 잘못된 인자(예: 재고 부족, 별점 범위 초과 등 IllegalArgumentException) → 400 Bad Request.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 위에서 못 잡은 예상 밖의 모든 예외 → 500 Internal Server Error.
    // 내부 상세(스택트레이스 등)는 노출하지 않고 일반적인 메시지만 준다(보안).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        ErrorResponse body = new ErrorResponse(status.value(), "서버 내부 오류가 발생했습니다.");
        return ResponseEntity.status(status).body(body);
    }
}
