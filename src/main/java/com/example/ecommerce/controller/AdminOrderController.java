package com.example.ecommerce.controller;

import com.example.ecommerce.domain.OrderStatus;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.dto.OrderStatusChangeRequest;
import com.example.ecommerce.dto.OrderSummaryResponse;
import com.example.ecommerce.service.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 어드민 주문 관리 API. /api/admin/** → ADMIN 전용.
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    // 주문 상태 변경. 허용 안 된 전이면 400.
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody OrderStatusChangeRequest request) {
        return adminOrderService.updateStatus(id, request);
    }

    // GET /api/admin/orders?status=PAID&page=0&size=20 — 전체 주문(상태 필터 + 페이징)
    @GetMapping
    public Page<OrderSummaryResponse> getOrders(@RequestParam(required = false) OrderStatus status,
                                                Pageable pageable) {
        return adminOrderService.getOrders(status, pageable);
    }
}
