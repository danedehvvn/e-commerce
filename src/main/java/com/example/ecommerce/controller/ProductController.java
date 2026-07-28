package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// @RestController : @Controller + @ResponseBody.
//   → 각 메서드의 반환값을 자동으로 JSON으로 직렬화해 응답 본문에 담는다.
// @RequestMapping("/api/products") : 이 컨트롤러의 모든 경로 앞에 붙는 공통 프리픽스.
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products/{id} — 상품 상세
    // @PathVariable : URL 경로의 {id}를 메서드 파라미터로 바인딩.
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    // GET /api/products?page=0&size=20&sort=price,desc&categoryId=1&keyword=머그
    // @RequestParam(required=false) : 쿼리 파라미터. 없어도 되게 null 허용.
    // Pageable : page/size/sort 쿼리 파라미터를 스프링이 알아서 묶어 만들어 주는 객체.
    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return productService.getProducts(categoryId, keyword, pageable);
    }
}
