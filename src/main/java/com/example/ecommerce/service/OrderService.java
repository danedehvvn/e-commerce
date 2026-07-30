package com.example.ecommerce.service;

import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.OrderStatus;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.OrderCreateRequest;
import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.global.exception.ForbiddenException;
import com.example.ecommerce.global.exception.MemberNotFoundException;
import com.example.ecommerce.global.exception.OrderNotFoundException;
import com.example.ecommerce.global.exception.ProductNotFoundException;
import com.example.ecommerce.repository.MemberRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        MemberRepository memberRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    // 주문 생성.
    // @Transactional(쓰기) : 아래 여러 작업(재고 차감 + 주문 저장)을 "하나의 원자적 단위"로 묶는다.
    //   중간에 하나라도 실패(예: 3번째 상품 재고 부족)하면 앞서 차감한 재고까지 전부 롤백된다.
    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 요청 항목마다: 상품 조회 → OrderItem 생성(이 안에서 재고 차감 + 주문 시점 가격 스냅샷)
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest item : request.items()) {
            // ★ 동시성 제어: 일반 findById가 아니라 비관적 락으로 조회한다.
            //   같은 상품을 동시에 주문하는 다른 트랜잭션은 이 행이 풀릴 때까지 대기 → 재고 차감이 직렬화됨.
            Product product = productRepository.findByIdForUpdate(item.productId())
                    .orElseThrow(() -> new ProductNotFoundException(item.productId()));

            // OrderItem.create가 product.decreaseStock(count)을 호출한다.
            //   재고 부족이면 여기서 IllegalArgumentException → 400 (그리고 트랜잭션 롤백)
            orderItems.add(OrderItem.create(product, item.quantity()));
        }

        // 주문 생성(총액 계산 + 연관관계 세팅). 간이 결제 완료 처리.
        Order order = Order.create(member, orderItems);
        order.changeStatus(OrderStatus.PAID);

        // cascade = ALL 이라 order만 저장해도 orderItems가 함께 저장된다.
        Order saved = orderRepository.save(order);

        return OrderResponse.from(saved);
    }

    // 내 주문 목록 (페이징)
    public Page<OrderResponse> getMyOrders(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return orderRepository.findByMember(member, pageable)
                .map(OrderResponse::from);
    }

    // 주문 상세. 본인 주문인지 검증한다.
    public OrderResponse getOrder(Long memberId, Long orderId) {
        // fetch join으로 주문 + 주문상품 + 상품을 "한 번의 쿼리"로 가져온다 (N+1 제거).
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 소유자 검증: 남의 주문은 볼 수 없다.
        if (!order.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("본인의 주문만 조회할 수 있습니다.");
        }

        return OrderResponse.from(order);
    }
}
