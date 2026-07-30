package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderCreateRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 주문 API. 인증 필요.
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders — 주문 생성 (성공 시 201)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@AuthenticationPrincipal Long memberId,
                                     @Valid @RequestBody OrderCreateRequest request) {
        return orderService.createOrder(memberId, request);
    }

    // GET /api/orders — 내 주문 목록 (페이징)
    @GetMapping
    public Page<OrderResponse> getMyOrders(@AuthenticationPrincipal Long memberId,
                                           Pageable pageable) {
        return orderService.getMyOrders(memberId, pageable);
    }

    // GET /api/orders/{id} — 주문 상세 (본인 주문만)
    @GetMapping("/{id}")
    public OrderResponse getOrder(@AuthenticationPrincipal Long memberId,
                                  @PathVariable Long id) {
        return orderService.getOrder(memberId, id);
    }
}

