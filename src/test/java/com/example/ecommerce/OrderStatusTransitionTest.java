package com.example.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.OrderStatus;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.global.exception.InvalidOrderStatusException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// 주문 상태 전이 규칙 단위 테스트.
// 스프링/DB 없이 순수 도메인 객체만으로 규칙을 검증한다(빠르고 의존성 없음).
class OrderStatusTransitionTest {

    // 테스트용 주문 하나 생성 (상태: PAYMENT_WAITING)
    private Order newOrder() {
        Category category = Category.create("테스트");
        Product product = Product.create(category, "상품", 1000, 100, "설명");
        Member member = Member.create("t@test.com", "pw12345678", "테스터");
        OrderItem item = OrderItem.create(product, 2);
        return Order.create(member, List.of(item));
    }

    @Test
    @DisplayName("정상 전이: 결제대기 → 결제완료 → 배송중 → 배송완료")
    void 정상_전이_흐름() {
        Order order = newOrder(); // PAYMENT_WAITING
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);

        order.changeStatus(OrderStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        order.changeStatus(OrderStatus.SHIPPING);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);

        order.changeStatus(OrderStatus.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배송중(SHIPPING) 주문은 취소할 수 없다")
    void 배송중_취소_불가() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.SHIPPING);

        assertThatThrownBy(() -> order.changeStatus(OrderStatus.CANCELED))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    @DisplayName("배송완료(DELIVERED) 주문은 이전 상태로 되돌릴 수 없다")
    void 배송완료_역행_불가() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.SHIPPING);
        order.changeStatus(OrderStatus.DELIVERED);

        assertThatThrownBy(() -> order.changeStatus(OrderStatus.PAYMENT_WAITING))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    @DisplayName("결제대기 → 배송중처럼 단계를 건너뛰는 전이는 불가")
    void 단계_건너뛰기_불가() {
        Order order = newOrder(); // PAYMENT_WAITING

        assertThatThrownBy(() -> order.changeStatus(OrderStatus.SHIPPING))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    @DisplayName("취소 시 차감했던 재고가 복구된다")
    void 취소시_재고_복구() {
        Category category = Category.create("테스트");
        Product product = Product.create(category, "상품", 1000, 100, "설명");
        Member member = Member.create("t@test.com", "pw12345678", "테스터");
        OrderItem item = OrderItem.create(product, 30); // 재고 100 → 70
        Order order = Order.create(member, List.of(item));
        assertThat(product.getStockQuantity()).isEqualTo(70);

        order.changeStatus(OrderStatus.CANCELED); // 결제대기 → 취소 (허용)

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(product.getStockQuantity()).isEqualTo(100); // 재고 복구됨
    }
}
