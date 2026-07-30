package com.example.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.OrderCreateRequest;
import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.MemberRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 재고 동시성 제어 검증 테스트.
// @SpringBootTest : 전체 컨텍스트를 띄워 "진짜 트랜잭션"이 스레드별로 커밋되게 한다.
//   (@DataJpaTest는 테스트를 하나의 트랜잭션으로 묶고 롤백해서 동시성 재현이 불가능하다)
@SpringBootTest
class OrderConcurrencyTest {

    @Autowired private OrderService orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MemberRepository memberRepository;

    private Long productId;
    private Long memberId;

    private static final int STOCK = 10;    // 한정 재고
    private static final int THREADS = 100; // 동시에 주문하는 사람 수

    @BeforeEach
    void setUp() {
        // 재고 10개짜리 상품 + 주문할 회원 1명을 미리 커밋해 둔다.
        Category category = categoryRepository.save(Category.create("동시성테스트"));
        Product product = productRepository.save(
                Product.create(category, "한정판 굿즈", 1000, STOCK, "재고 10개 한정"));
        this.productId = product.getId();

        Member member = memberRepository.save(
                Member.create("concurrency@test.com", "pw12345678", "동시성테스터"));
        this.memberId = member.getId();
    }

    @Test
    @DisplayName("100명이 동시에 주문해도, 재고 10개만 정확히 팔린다(초과 판매 없음)")
    void 동시_주문_재고_초과판매_없음() throws InterruptedException {
        // 스레드 32개를 돌리는 풀에 100개의 주문 작업을 던진다.
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREADS); // 모든 작업이 끝날 때까지 기다리는 문
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    OrderCreateRequest request = new OrderCreateRequest(
                            List.of(new OrderItemRequest(productId, 1)));
                    orderService.createOrder(memberId, request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 재고 부족 등으로 실패한 주문
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 100개 작업이 전부 끝날 때까지 대기
        executor.shutdown();

        Product product = productRepository.findById(productId).orElseThrow();
        System.out.println(">>> 성공 주문=" + successCount.get()
                + " / 실패 주문=" + failCount.get()
                + " / 남은 재고=" + product.getStockQuantity());

        // 재고 10개면 정확히 10건만 성공하고, 재고는 0이어야 한다.
        assertThat(successCount.get())
                .as("성공한 주문 수는 재고(%d)와 정확히 같아야 한다", STOCK)
                .isEqualTo(STOCK);
        assertThat(product.getStockQuantity())
                .as("남은 재고는 정확히 0이어야 한다(음수/초과판매 금지)")
                .isEqualTo(0);
    }
}
