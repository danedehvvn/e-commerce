package com.example.ecommerce.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 어드민 전용 영역. SecurityConfig에서 /api/admin/** 는 hasRole("ADMIN")으로 보호된다.
// (실제 어드민 비즈니스 로직은 다음 단계. 지금은 권한 동작 확인용 핑 하나만)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // GET /api/admin/ping — ADMIN 권한이 있어야만 200. USER면 403.
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "admin ok");
    }
}
