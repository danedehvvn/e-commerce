package com.example.ecommerce.global.security;

import com.example.ecommerce.global.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security 6 방식: WebSecurityConfigurerAdapter(구식) 대신
//   SecurityFilterChain 빈을 직접 등록하고 람다 DSL로 설정한다.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint; // 401
    private final JwtAccessDeniedHandler accessDeniedHandler;           // 403

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화: CSRF 공격은 브라우저가 쿠키를 자동 전송하는 걸 악용한다.
                //   우리는 세션·쿠키가 아니라 JWT를 헤더에 직접 실어 보내므로 그 공격이 성립하지 않는다 → 꺼도 안전.
                .csrf(csrf -> csrf.disable())

                // 세션을 아예 만들지 않는다(STATELESS). JWT에 상태가 다 들어있어 서버가 세션을 기억할 필요가 없다.
                //   → 서버 확장(여러 대) 시에도 세션 공유 문제가 없다.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // REST API라 폼 로그인/HTTP Basic 로그인 화면은 쓰지 않는다.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ── 엔드포인트별 접근 권한 ──
                .authorizeHttpRequests(auth -> auth
                        // 상품·카테고리 "조회"(GET)는 누구나
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
                        // 회원가입·로그인은 누구나
                        .requestMatchers("/api/auth/**").permitAll()
                        // 어드민 전용 경로는 ADMIN 권한만 (다음 단계에서 채워짐)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated())

                // 401/403을 우리 JSON 형식으로 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 안 됨 → 401
                        .accessDeniedHandler(accessDeniedHandler))          // 권한 없음 → 403

                // 우리 JWT 필터를 스프링 기본 인증 필터(UsernamePasswordAuthenticationFilter) "앞"에 끼운다.
                //   → 컨트롤러에 닿기 전에 토큰을 검증해 SecurityContext를 채운다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
