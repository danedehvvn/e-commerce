package com.example.ecommerce.service;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.global.exception.ProductNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service : 이 클래스가 비즈니스 로직 계층임을 스프링에 알린다(빈으로 등록).
// @Transactional(readOnly = true)
//   : 이 클래스의 모든 메서드를 "읽기 전용 트랜잭션"으로 실행.
//     - 트랜잭션이 열려 있으므로 LAZY 필드(getCategory())에 안전하게 접근 → DTO 변환 가능.
//     - readOnly=true는 조회 성능 최적화 힌트(불필요한 변경 감지 스냅샷 생략).
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // ── 생성자 주입 ──
    // 필드에 @Autowired를 붙이지 않고 생성자로 주입한다.
    //   - final로 둘 수 있어 불변 + 의존성이 명확 + 테스트 시 가짜 객체 넣기 쉬움.
    //   - 생성자가 하나면 @Autowired 생략 가능(스프링이 자동 주입).
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 상세 조회. 없으면 예외 → (전역 처리기가 404로 변환)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    // 상품 목록 조회 (페이징 + 필터 + 검색)
    // 우선순위: 카테고리 필터 > 검색 > 전체.
    public Page<ProductResponse> getProducts(Long categoryId, String keyword, Pageable pageable) {
        Page<Product> products;

        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContaining(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        // Page.map : 페이징 정보(전체 개수 등)는 유지한 채 내용물만 엔티티 → DTO로 변환.
        return products.map(ProductResponse::from);
    }
}
