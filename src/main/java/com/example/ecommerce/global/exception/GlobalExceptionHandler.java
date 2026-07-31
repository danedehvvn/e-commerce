package com.example.ecommerce.global.exception;

import com.example.ecommerce.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // 로그인 실패(이메일 없음/비밀번호 불일치) → 401 Unauthorized.
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 이미 존재하는 이메일로 회원가입 → 409 Conflict.
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException e) {
        HttpStatus status = HttpStatus.CONFLICT; // 409
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // @Valid 검증 실패(이메일 형식 오류, 비밀번호 길이 등) → 400 Bad Request.
    // 첫 번째 필드 에러 메시지를 뽑아 응답에 담는다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        ErrorResponse body = new ErrorResponse(status.value(), message);
        return ResponseEntity.status(status).body(body);
    }

    // 회원 없음 → 404 Not Found.
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 카테고리 없음 → 404 Not Found.
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 주문 없음 → 404 Not Found.
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 장바구니 항목 없음 → 404 Not Found.
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 소유자가 아님(남의 자원 조작) → 403 Forbidden.
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 403
        ErrorResponse body = new ErrorResponse(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 허용되지 않은 주문 상태 전이 → 400 Bad Request.
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(InvalidOrderStatusException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
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
