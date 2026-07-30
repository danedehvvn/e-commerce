package com.example.ecommerce.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// PasswordEncoder 빈을 별도 설정 클래스로 분리한다.
//
// 왜 SecurityConfig가 아니라 여기에?
//   SecurityConfig 안에 PasswordEncoder를 두면, SecurityConfig → (다른 빈) → PasswordEncoder 처럼
//   서로를 필요로 하다 순환 참조(circular dependency)가 생기기 쉽다.
//   PasswordEncoder를 독립된 설정으로 빼두면 그 위험이 사라진다. (스프링 시큐리티의 흔한 함정 회피)
@Configuration
public class EncoderConfig {

    // @Bean : 이 메서드가 반환하는 객체를 스프링 컨테이너에 등록한다.
    //   이후 어디서든 PasswordEncoder를 주입받아 쓸 수 있다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt : 비밀번호 해싱 표준 알고리즘.
        return new BCryptPasswordEncoder();
    }
}
