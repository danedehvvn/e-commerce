package com.example.ecommerce.global;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 모든 엔티티가 공통으로 가질 "생성시각/수정시각"을 한곳에 모은 부모 클래스.
//
// @MappedSuperclass : 이 클래스는 테이블이 되지 않는다.
//   대신 이 클래스를 상속한 엔티티의 테이블에 createdAt/updatedAt "컬럼"만 내려준다.
//   (상속으로 중복 코드를 없애는 JPA의 방법)
//
// @EntityListeners(AuditingEntityListener.class)
//   : 엔티티가 저장/수정될 때 아래 시각 필드를 자동으로 채워주는 "리스너"를 붙인다.
//     (메인 클래스의 @EnableJpaAuditing과 짝을 이룬다)
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    // @CreatedDate : INSERT 될 때 현재 시각이 자동으로 들어간다.
    // updatable = false : 한 번 저장된 뒤로는 절대 수정되지 않게 막는다.
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // @LastModifiedDate : INSERT/UPDATE 될 때마다 현재 시각으로 갱신된다.
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
