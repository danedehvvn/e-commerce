package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 회원의 주문 목록. (마이페이지 주문내역 등)
    List<Order> findByMember(Member member);

    // 특정 회원의 주문 목록 (페이징)
    Page<Order> findByMember(Member member, Pageable pageable);

    // ── 주문 상세를 fetch join으로 한 번에 조회 ──
    // join fetch로 orderItems와 각 item의 product까지 "하나의 SELECT"로 가져온다.
    //   → 주문 상세 조회 시 발생하던 N+1(주문상품/상품마다 추가 쿼리)을 제거한다.
    @Query("select o from Order o "
            + "join fetch o.orderItems oi "
            + "join fetch oi.product "
            + "where o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
