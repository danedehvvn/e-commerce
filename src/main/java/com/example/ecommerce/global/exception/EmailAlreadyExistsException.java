package com.example.ecommerce.global.exception;

// 이미 가입된 이메일로 또 회원가입을 시도했을 때 던지는 예외.
// 전역 처리기에서 409 Conflict로 변환한다.
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("이미 사용 중인 이메일입니다. email=" + email);
    }
}
