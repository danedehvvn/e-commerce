package com.example.ecommerce.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// 커스텀 JWT 인증 필터.
//
// OncePerRequestFilter 를 상속하는 이유:
//   한 번의 요청이 내부적으로 여러 번 디스패치(forward/include 등)될 수 있는데,
//   그때마다 인증 로직이 중복 실행되지 않도록 "요청당 딱 한 번"만 돌게 보장해준다.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) 요청 헤더에서 토큰 추출 (Authorization: Bearer xxx)
        String token = resolveToken(request);

        // 2) 토큰이 있고 유효하면 → 인증 객체를 만들어 SecurityContext에 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long memberId = jwtTokenProvider.getMemberId(token);
            String role = jwtTokenProvider.getRole(token);

            // 권한은 "ROLE_" 접두사 관례를 따른다. (hasRole("ADMIN") → 내부적으로 ROLE_ADMIN 확인)
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // principal(주체)로 회원 id를 담는다 → 컨트롤러에서 @AuthenticationPrincipal로 꺼내 쓸 수 있음.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, null, authorities);

            // ★ 이 한 줄이 핵심: "이 요청은 인증된 사용자"라는 사실을 저장.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3) 다음 필터로 넘긴다.
        //   토큰이 없거나 무효여도 여기서 막지 않는다 → 인가는 뒤의 AuthorizationFilter가 규칙대로 판단.
        //   (permitAll 경로는 인증 없이도 통과해야 하니까)
        filterChain.doFilter(request, response);
    }

    // "Authorization: Bearer <token>" 헤더에서 토큰 문자열만 잘라낸다.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 다음부터가 실제 토큰
        }
        return null;
    }
}
