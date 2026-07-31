package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductCreateRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.dto.ProductStatusChangeRequest;
import com.example.ecommerce.dto.ProductUpdateRequest;
import com.example.ecommerce.dto.StockAddRequest;
import com.example.ecommerce.service.AdminProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 어드민 상품 관리 API. 경로가 /api/admin/** 이라 SecurityConfig의 hasRole("ADMIN")로 보호된다.
//   → USER 토큰으로 접근하면 403.
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    // 상품 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return adminProductService.createProduct(request);
    }

    // 상품 정보 수정 (부분 수정 의미의 PATCH)
    @PatchMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody ProductUpdateRequest request) {
        return adminProductService.updateProduct(id, request);
    }

    // 재고 입고
    @PatchMapping("/{id}/stock")
    public ProductResponse addStock(@PathVariable Long id,
                                    @Valid @RequestBody StockAddRequest request) {
        return adminProductService.addStock(id, request);
    }

    // 판매 상태 변경
    @PatchMapping("/{id}/status")
    public ProductResponse changeStatus(@PathVariable Long id,
                                        @Valid @RequestBody ProductStatusChangeRequest request) {
        return adminProductService.changeStatus(id, request);
    }

    // GET /api/admin/products/low-stock?threshold=10 — 재고 부족 상품 조회
    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        return adminProductService.getLowStockProducts(threshold);
    }
}
