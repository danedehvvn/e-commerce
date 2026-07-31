package com.example.ecommerce.service;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.ProductCreateRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.ProductStatusChangeRequest;
import com.example.ecommerce.dto.ProductUpdateRequest;
import com.example.ecommerce.dto.StockAddRequest;
import com.example.ecommerce.global.exception.CategoryNotFoundException;
import com.example.ecommerce.global.exception.ProductNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 어드민 상품 관리 서비스. (구매자용 조회는 ProductService, 운영자용 쓰기는 여기로 분리)
@Service
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(ProductRepository productRepository,
                               CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // 상품 등록
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product product = productRepository.save(Product.create(
                category, request.name(), request.price(), request.stockQuantity(), request.description()));
        return ProductResponse.from(product);
    }

    // 상품 정보 수정
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProduct(productId);
        // 엔티티의 의미 있는 메서드로 변경 (변경 감지로 자동 UPDATE)
        product.updateInfo(request.name(), request.price(), request.description());
        return ProductResponse.from(product);
    }

    // 재고 입고
    @Transactional
    public ProductResponse addStock(Long productId, StockAddRequest request) {
        Product product = findProduct(productId);
        product.addStock(request.quantity());
        return ProductResponse.from(product);
    }

    // 판매 상태 변경
    @Transactional
    public ProductResponse changeStatus(Long productId, ProductStatusChangeRequest request) {
        Product product = findProduct(productId);
        product.changeStatus(request.status());
        return ProductResponse.from(product);
    }

    // 재고 부족 상품 조회 (재고 <= threshold, 적은 순)
    public List<ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThanEqualOrderByStockQuantityAsc(threshold)
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
