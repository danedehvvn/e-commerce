package com.example.ecommerce.dto;

import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Role;

// 회원 "응답" DTO. 절대 password를 담지 않는다(민감정보 노출 금지).
public record MemberResponse(
        Long id,
        String email,
        String name,
        Role role
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
