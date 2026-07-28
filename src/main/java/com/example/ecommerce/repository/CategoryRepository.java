package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본 CRUD만으로 충분해서 추가 메서드 없음.
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
