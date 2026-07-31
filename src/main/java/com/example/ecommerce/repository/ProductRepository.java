package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 재고가 임계값 이하인 상품(재고 적은 순). 운영자가 재입고 판단에 쓴다.
    List<Product> findByStockQuantityLessThanEqualOrderByStockQuantityAsc(int threshold);

    // ── 비관적 쓰기 락(Pessimistic Write Lock)으로 상품 조회 ──
    // @Lock(PESSIMISTIC_WRITE) → 실행되는 SQL이 "SELECT ... FOR UPDATE"가 된다.
    //   이 행(상품)을 조회하는 순간 DB가 행을 잠그고, 트랜잭션이 끝날 때까지
    //   다른 트랜잭션은 같은 행을 잠그지 못하고 "대기"한다. → 재고 차감이 한 번에 하나씩 순서대로 처리됨.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
