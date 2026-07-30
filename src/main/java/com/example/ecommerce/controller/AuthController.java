package com.example.ecommerce.controller;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.MemberResponse;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.dto.TokenResponse;
import com.example.ecommerce.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 인증 관련 API(회원가입/로그인)를 모아두는 컨트롤러.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    // POST /api/auth/signup — 회원가입
    // @Valid : SignupRequest에 붙은 검증 어노테이션(@Email 등)을 실제로 검사하게 만든다.
    // @RequestBody : 요청 본문(JSON)을 SignupRequest 객체로 역직렬화.
    // @ResponseStatus(CREATED) : 성공 시 201 Created 반환(리소스 생성 의미).
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse signup(@Valid @RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    // POST /api/auth/login — 로그인 후 JWT 발급. 성공 시 200 OK + 토큰.
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return memberService.login(request);
    }
}
