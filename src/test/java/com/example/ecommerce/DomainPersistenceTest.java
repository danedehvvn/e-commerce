package com.example.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.ProductStatus;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.MemberRepository;
import com.example.ecommerce.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// @DataJpaTest : JPA 관련 부품(엔티티, 리포지토리)만 로딩하는 "슬라이스 테스트".
//   - 별도 설정 없이 H2 메모리 DB를 자동으로 띄워 붙여준다. (Docker 불필요)
//   - 각 테스트가 끝나면 자동 롤백되어 서로 영향을 주지 않는다.
@DataJpaTest
class DomainPersistenceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("회원·카테고리·상품을 저장하고 다시 조회할 수 있다")
    void 저장_조회() {
        // given : 팩토리 메서드로 엔티티 생성 후 저장
        Member member = memberRepository.save(Member.create("test@example.com", "pw1234", "홍길동"));
        Category category = categoryRepository.save(Category.create("문구"));
        Product product = productRepository.save(
                Product.create(category, "3색 볼펜", 1500, 50, "부드럽게 써지는 볼펜"));

        // when : 이메일로 회원 조회 (쿼리 메서드), 판매중 상품 목록 조회
        Optional<Member> found = memberRepository.findByEmail("test@example.com");
        List<Product> onSale = productRepository.findByStatus(ProductStatus.ON_SALE);

        // then : 값 검증
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("홍길동");

        assertThat(onSale).hasSize(1);
        assertThat(onSale.get(0).getName()).isEqualTo("3색 볼펜");
        // 연관관계(Product → Category)가 잘 연결됐는지
        assertThat(onSale.get(0).getCategory().getName()).isEqualTo("문구");
        // 생성 시 기본 상태가 판매중인지
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);

        // BaseTimeEntity의 JPA Auditing이 동작해 생성시각이 자동으로 채워졌는지
        assertThat(member.getCreatedAt()).isNotNull();
    }
}
