package com.example.ecommerce.dto;

// 로그인 성공 시 돌려줄 "응답" DTO.
// tokenType "Bearer" : 클라이언트가 Authorization: Bearer <accessToken> 형태로 보내야 함을 알려준다.
public record TokenResponse(
        String tokenType,
        String accessToken
) {
    public static TokenResponse bearer(String accessToken) {
        return new TokenResponse("Bearer", accessToken);
    }
}
