package com.example.ecommerce.global.exception;

// 회원을 찾을 수 없음 → 404. (예: 토큰은 유효하나 해당 회원이 삭제된 경우)
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long memberId) {
        super("회원을 찾을 수 없습니다. id=" + memberId);
    }
}
