package com.example.ecommerce.domain;

import com.example.ecommerce.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// @Entity : 이 클래스를 DB 테이블과 매핑한다. (테이블명 미지정 시 클래스명 → member 테이블)
// BaseTimeEntity 상속 → createdAt/updatedAt 컬럼을 자동으로 물려받는다.
@Entity
@Getter
// @NoArgsConstructor(PROTECTED) : JPA는 엔티티를 만들 때 "기본 생성자"가 반드시 필요하다.
//   하지만 아무나 new Member() 로 빈 객체를 만들면 이메일 없는 회원 같은 잘못된 상태가 생김.
//   그래서 기본 생성자를 만들되 protected로 막아, 외부에서 직접 못 만들게 한다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    // @Id : 이 필드가 기본키(PK).
    // @GeneratedValue(IDENTITY) : PK 생성을 DB에 맡긴다 → MySQL의 AUTO_INCREMENT 사용.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true : 이메일 중복 가입 방지 (DB 레벨에서 유니크 제약)
    // nullable = false : NOT NULL 제약
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // enum은 반드시 STRING으로 저장 (ORDINAL의 순서 오염 문제 방지)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // @Builder : 필드가 많을 때 "무슨 값을 넣는지" 이름으로 명확히 채우는 생성 방식.
    //   생성자를 private로 두고 빌더로만 만들게 해서, 생성 경로를 통제한다.
    @Builder
    private Member(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // 정적 팩토리 메서드 : "회원가입"이라는 의미가 드러나는 생성 통로.
    //   가입 시 권한은 항상 USER로 고정한다. (외부에서 ADMIN으로 못 만들게)
    public static Member create(String email, String password, String name) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(Role.USER)
                .build();
    }

    // 관리자 계정 생성 통로. 일반 회원가입(create)과 분리해, ADMIN은 이 메서드로만 만들 수 있게 한다.
    //   (운영에서는 시드/마이그레이션 등 통제된 경로에서만 호출)
    public static Member createAdmin(String email, String password, String name) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(Role.ADMIN)
                .build();
    }
}
