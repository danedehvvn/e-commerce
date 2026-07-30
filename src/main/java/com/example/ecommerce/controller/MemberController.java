package com.example.ecommerce.controller;

import com.example.ecommerce.dto.MemberResponse;
import com.example.ecommerce.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // GET /api/members/me — 내 정보 조회 (인증 필요)
    //
    // @AuthenticationPrincipal
    //   : SecurityContext에 저장된 principal을 꺼내 파라미터로 주입한다.
    //     우리 JWT 필터가 principal로 "회원 id(Long)"를 넣었으므로, 여기서 그 id가 바로 들어온다.
    //   → 컨트롤러가 요청 헤더를 직접 파싱하지 않아도 "현재 로그인 사용자"를 알 수 있다.
    @GetMapping("/me")
    public MemberResponse getMyInfo(@AuthenticationPrincipal Long memberId) {
        return memberService.getMyInfo(memberId);
    }
}
