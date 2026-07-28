# e-commerce (잡화 커머스)

Java · Spring Boot 기반 잡화 커머스 백엔드 포트폴리오. 상품 · 장바구니 · 주문 · 어드민을 다루며,
**주문 시 재고 차감 동시성 제어 / N+1 문제 해결 / 운영자 어드민**을 깊게 파는 것을 목표로 한다.

## 기술 스택
- Java 17, Spring Boot 3.4
- Spring Data JPA (Hibernate), Validation, Lombok
- MySQL 8 (운영/개발), H2 (테스트)
- Gradle (Wrapper 포함)

## 현재까지 (1단계)
- 프로젝트 세팅, DB 연결 설정
- 도메인 엔티티 7종 + 연관관계 설계
  - `Member`, `Category`, `Product`, `CartItem`, `Order`, `OrderItem`, `Review`
  - 모든 `@ManyToOne`은 `LAZY`, 양방향은 `Order ↔ OrderItem` 뿐
- 기본 Repository, H2 기반 동작 확인 테스트

## 실행 방법

### 1) 로컬 MySQL 띄우기 (Docker)
```bash
docker compose up -d
```
- MySQL 8이 `localhost:3306`에 뜬다. DB: `commerce`, 계정: `commerce / commerce1234`

### 2) 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3) 테스트 (H2 메모리 DB, Docker 불필요)
```bash
./gradlew test
```

## 패키지 구조
```
com.example.ecommerce
├── domain/       # 엔티티 + Enum
├── repository/   # Spring Data JPA Repository
├── service/      # 비즈니스 로직 (예정)
├── controller/   # REST API (예정)
├── dto/          # 요청·응답 DTO (예정)
└── global/       # 공통 (BaseTimeEntity 등)
```
