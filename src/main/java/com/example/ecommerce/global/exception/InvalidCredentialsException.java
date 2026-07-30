package com.example.ecommerce.global.exception;

// 로그인 실패(이메일 없음 또는 비밀번호 불일치) 예외 → 401 Unauthorized.
//
// ※ 보안 포인트: "이메일이 없음"과 "비밀번호 틀림"을 구분해서 알려주면
//   공격자가 "이 이메일은 가입돼 있구나"를 알아낼 수 있다(계정 존재 여부 유출).
//   그래서 둘 다 똑같은 예외 + 똑같은 메시지로 처리한다.
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
