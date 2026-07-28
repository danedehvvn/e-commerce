package com.example.ecommerce.repository;

import com.example.ecommerce.domain.CartItem;
import com.example.ecommerce.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 회원의 장바구니 전체 조회. findByMember → WHERE member_id = ?
    List<CartItem> findByMember(Member member);

    // 회원+상품 조합으로 이미 담긴 항목이 있는지 찾기 (있으면 수량만 더하려고).
    Optional<CartItem> findByMemberAndProduct(Member member, com.example.ecommerce.domain.Product product);
}
