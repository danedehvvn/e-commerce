package com.example.ecommerce.dto;

import com.example.ecommerce.domain.Category;

// 카테고리 "응답 전용" 데이터 그릇(DTO).
// record : 값만 담는 불변 객체를 짧게 정의하는 자바 16+ 문법.
//   생성자·getter·equals·toString을 자동으로 만들어준다. (DTO에 딱 맞음)
public record CategoryResponse(
        Long id,
        String name
) {
    // 엔티티 → DTO 변환은 "DTO가 자기 자신을 만드는" 정적 팩토리로.
    // 이렇게 하면 변환 규칙이 DTO 한곳에 모여, 컨트롤러/서비스가 지저분해지지 않는다.
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
