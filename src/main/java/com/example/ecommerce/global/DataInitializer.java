package com.example.ecommerce.global;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// CommandLineRunner : 앱이 완전히 뜬 직후 run(...)이 한 번 실행된다.
//   → 개발용 초기 데이터(시드)를 넣기에 딱 좋다.
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 이미 데이터가 있으면 다시 넣지 않는다(앱을 재시작해도 중복 방지 = 멱등).
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
