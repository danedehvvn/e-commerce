package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryResponse;
import com.example.ecommerce.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 전체 카테고리 목록. 개수가 적어 페이징 없이 List로 반환.
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)   // 엔티티 → DTO 변환
                .toList();
    }
}
