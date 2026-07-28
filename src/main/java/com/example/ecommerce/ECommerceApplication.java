package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing : BaseTimeEntity의 createdAt/updatedAt 자동 기록 기능을 켠다.
//   (이게 없으면 @CreatedDate 등이 동작하지 않는다)
@EnableJpaAuditing
// @SpringBootApplication : 스프링 부트의 핵심 어노테이션 3개를 합친 것.
//   - @Configuration      : 설정 클래스
//   - @EnableAutoConfiguration : 의존성 보고 필요한 설정 자동 구성 (내장 톰캣 등)
//   - @ComponentScan      : 이 패키지 하위의 @Component/@Service 등을 찾아 등록
@SpringBootApplication
public class ECommerceApplication {

    // 자바 프로그램의 시작점. Node의 index.js에서 app.listen() 하는 부분에 해당.
    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }
}
