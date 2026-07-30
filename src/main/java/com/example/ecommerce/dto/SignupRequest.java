package com.example.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 회원가입 "요청" DTO.
// 각 필드에 검증 어노테이션을 달면, 컨트롤러에서 @Valid로 자동 검증된다.
// (조건 위반 시 MethodArgumentNotValidException → 전역 처리기에서 400으로 변환)
public record SignupRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        String name
) {
}
