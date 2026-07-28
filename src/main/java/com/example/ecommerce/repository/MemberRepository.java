package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티 타입, PK 타입>
//   → 상속만 해도 save, findById, findAll, delete 등 기본 CRUD가 전부 제공된다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // ── 쿼리 메서드 ──
    // 메서드 "이름"을 규칙대로 지으면, Spring Data JPA가 그 이름을 해석해 쿼리를 자동 생성한다.
    // findByEmail → SELECT * FROM member WHERE email = ?
    // 결과가 없을 수도 있으니 Optional로 감싸 "없음"을 명시적으로 표현한다.
    Optional<Member> findByEmail(String email);

    // existsByEmail → 존재 여부만 boolean으로. 회원가입 시 이메일 중복 체크에 쓴다.
    boolean existsByEmail(String email);
}
