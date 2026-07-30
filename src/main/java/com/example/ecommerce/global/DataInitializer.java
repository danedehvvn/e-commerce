package com.example.ecommerce.global;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.MemberRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// CommandLineRunner : 앱이 완전히 뜬 직후 run(...)이 한 번 실행된다.
//   → 개발용 초기 데이터(시드)를 넣기에 딱 좋다.
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 시드 계정 비밀번호도 BCrypt로 암호화해 저장

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           MemberRepository memberRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initMembers();
        initProducts();
    }

    // 테스트용 계정 시드 (멱등: 이미 있으면 건너뜀)
    //   - 관리자: admin@example.com / admin1234  (role=ADMIN)
    //   - 일반: user@example.com / user1234     (role=USER)
    private void initMembers() {
        if (!memberRepository.existsByEmail("admin@example.com")) {
            memberRepository.save(Member.createAdmin(
                    "admin@example.com", passwordEncoder.encode("admin1234"), "관리자"));
        }
        if (!memberRepository.existsByEmail("user@example.com")) {
            memberRepository.save(Member.create(
                    "user@example.com", passwordEncoder.encode("user1234"), "일반회원"));
        }
    }

    private void initProducts() {
        // 이미 상품이 있으면 다시 넣지 않는다.
        if (categoryRepository.count() > 0) {
            return;
        }

        // 카테고리 3개
        Category kitchen = categoryRepository.save(Category.create("주방"));
        Category stationery = categoryRepository.save(Category.create("문구"));
        Category bath = categoryRepository.save(Category.create("욕실"));

        // 상품 여러 개 (가격이 섞이도록 = 정렬 확인용)
        productRepository.save(Product.create(kitchen, "화이트 머그컵", 8900, 120, "심플한 도자기 머그"));
        productRepository.save(Product.create(kitchen, "우드 도마", 15900, 40, "원목 도마"));
        productRepository.save(Product.create(kitchen, "스텐 텀블러 머그", 12900, 0, "보온 텀블러형 머그"));
        productRepository.save(Product.create(stationery, "3색 볼펜", 1500, 300, "부드러운 볼펜"));
        productRepository.save(Product.create(stationery, "A5 노트", 3200, 200, "무지 노트"));
        productRepository.save(Product.create(stationery, "떡메모지", 2500, 150, "귀여운 메모지"));
        productRepository.save(Product.create(bath, "대나무 칫솔", 2900, 80, "친환경 칫솔"));
        productRepository.save(Product.create(bath, "샤워 타월", 4500, 60, "때수건"));
    }
}
