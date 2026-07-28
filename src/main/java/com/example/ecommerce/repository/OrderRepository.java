package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 회원의 주문 목록. (마이페이지 주문내역 등)
    List<Order> findByMember(Member member);
}
