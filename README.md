# e-commerce (잡화 커머스)

Java · Spring Boot 기반 잡화 커머스 백엔드 포트폴리오. 상품 · 장바구니 · 주문 · 어드민을 다루며,
**주문 시 재고 차감 동시성 제어 / N+1 문제 해결 / 운영자 어드민**을 깊게 파는 것을 목표로 한다.

## 기술 스택
- Java 17, Spring Boot 3.4
- Spring Data JPA (Hibernate), Validation, Lombok
- MySQL 8 (운영/개발), H2 (테스트)
- Gradle (Wrapper 포함)

## 현재까지
**1단계 — 도메인 설계**
- 프로젝트 세팅, DB 연결 설정
- 도메인 엔티티 7종 + 연관관계 설계
  - `Member`, `Category`, `Product`, `CartItem`, `Order`, `OrderItem`, `Review`
  - 모든 `@ManyToOne`은 `LAZY`, 양방향은 `Order ↔ OrderItem` 뿐
- 기본 Repository, H2 기반 동작 확인 테스트

**2단계 — 상품·카테고리 조회 API**
- Controller → Service → Repository 3계층
- 엔티티 → DTO 변환(`ProductResponse.from(...)`)
- 상품 목록 페이징·정렬(`Pageable`) + 카테고리 필터 + 이름 검색
- `@RestControllerAdvice` 전역 예외 처리 + 통일된 에러 응답

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
├── service/      # 비즈니스 로직
├── controller/   # REST API
├── dto/          # 요청·응답 DTO
└── global/       # 공통 (BaseTimeEntity, 전역 예외 처리, 시드 데이터)
```

## API 문서

| 메서드 | 경로 | 설명 | 주요 파라미터 |
|---|---|---|---|
| GET | `/api/products` | 상품 목록(페이징) | `page`, `size`, `sort`(예: `price,desc`), `categoryId`, `keyword` |
| GET | `/api/products/{id}` | 상품 상세 | — (없는 id면 404) |
| GET | `/api/categories` | 카테고리 전체 | — |

### 응답 예시

`GET /api/products?page=0&size=5&sort=price,desc`
```json
{
  "content": [
    { "id": 2, "name": "우드 도마", "price": 15900, "stockQuantity": 40,
      "status": "ON_SALE", "categoryId": 1, "categoryName": "주방" }
  ],
  "totalElements": 8,
  "totalPages": 2,
  "number": 0,
  "size": 5,
  "first": true,
  "last": false
}
```

`GET /api/products/9999` (없는 상품)
```json
{ "status": 404, "message": "상품을 찾을 수 없습니다. id=9999" }
```

### curl 예시
```bash
# 페이징 + 가격 내림차순 정렬
curl "http://localhost:8080/api/products?page=0&size=5&sort=price,desc"

# 카테고리 필터
curl "http://localhost:8080/api/products?categoryId=1"

# 이름 검색
curl "http://localhost:8080/api/products?keyword=머그"

# 상품 상세 / 없는 상품(404)
curl "http://localhost:8080/api/products/1"
curl -i "http://localhost:8080/api/products/9999"

# 카테고리 목록
curl "http://localhost:8080/api/categories"
```

> 앱 실행 시 `DataInitializer`가 카테고리 3종 + 상품 8개를 자동으로 넣는다(비어 있을 때만).
