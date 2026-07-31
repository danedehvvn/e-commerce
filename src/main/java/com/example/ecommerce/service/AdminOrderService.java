package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderStatus;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.dto.OrderStatusChangeRequest;
import com.example.ecommerce.dto.OrderSummaryResponse;
import com.example.ecommerce.global.exception.OrderNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 어드민 주문 관리 서비스.
@Service
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 주문 상태 변경. 허용 안 된 전이면 도메인(Order.changeStatus)이 예외를 던진다 → 400.
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatusChangeRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 검증은 서비스가 아니라 도메인이 수행한다(상태 규칙의 응집).
        order.changeStatus(request.status());

        return OrderResponse.from(order);
    }

    // 전체 주문 조회 (상태 필터 + 페이징). status가 없으면 전체.
    public Page<OrderSummaryResponse> getOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);
        return orders.map(OrderSummaryResponse::from);
    }
}
