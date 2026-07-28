package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 특정 상태(예: 판매중)인 상품만 목록으로. findByStatus → WHERE status = ?
    List<Product> findByStatus(ProductStatus status);

    // ── 페이징 조회 메서드 ──
    // Pageable을 파라미터로, Page를 반환 타입으로 두면 Spring Data가
    // WHERE 조건 + LIMIT/OFFSET + 전체 개수 COUNT 쿼리까지 알아서 처리한다.

    // 카테고리별 필터. Product.category.id 를 따라가 WHERE category_id = ?
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // 상품명 부분 일치 검색. WHERE name LIKE %keyword%
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
}
