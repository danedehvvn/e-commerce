package com.example.ecommerce.global.jwt;

import com.example.ecommerce.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// JWT 토큰을 "만들고(create) / 검증하고(validate) / 내용을 꺼내는(parse)" 도구 모음.
// @Component : 스프링 빈으로 등록해 다른 곳에서 주입받아 쓴다.
@Component
public class JwtTokenProvider {

    private final SecretKey key;          // 서명·검증에 쓰는 비밀 키
    private final long expirationMillis;  // 토큰 유효기간(ms)

    // 생성자에서 설정값(application.yml의 jwt.*)을 주입받는다.
    // @Value("${...}") : 설정 파일/환경변수의 값을 꺼내 넣어준다.
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis) {
        // 문자열 secret을 HMAC-SHA 키 객체로 변환. (HS256은 최소 32바이트 필요)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    // ── 토큰 생성 ──
    // subject(주체)에 회원 id, 커스텀 클레임에 role을 담는다.
    public String createToken(Long memberId, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 누구인지 (sub 클레임)
                .claim("role", role.name())        // 권한 (커스텀 클레임)
                .issuedAt(now)                     // 발급 시각
                .expiration(expiry)                // 만료 시각
                .signWith(key)                     // 이 키로 서명 → 위변조 방지
                .compact();                        // 최종 문자열로 직렬화
    }

    // ── 토큰 유효성 검증 ──
    // 서명이 위조됐거나 만료됐거나 형식이 깨지면 예외가 나므로, 그걸 잡아 false로.
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── 토큰에서 회원 id 꺼내기 ──
    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // ── 토큰에서 role 꺼내기 ──
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 서명을 검증하면서 토큰 본문(클레임)을 파싱한다.
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)              // 이 키로 서명 검증
                .build()
                .parseSignedClaims(token)     // 서명된 토큰 파싱 (틀리면 예외)
                .getPayload();
    }
}
