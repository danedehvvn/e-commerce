package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 특정 상태(예: 판매중)인 상품만 목록으로. findByStatus → WHERE status = ?
    List<Product> findByStatus(ProductStatus status);
}
