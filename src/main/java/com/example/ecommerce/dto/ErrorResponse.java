package com.example.ecommerce.dto;

// 에러가 났을 때 클라이언트에게 돌려줄 "통일된" 응답 형식.
// 예: { "status": 404, "message": "상품을 찾을 수 없습니다. id=9999" }
//
// 모든 에러가 같은 모양으로 나가야 프론트엔드가 한 가지 방식으로 처리할 수 있다.
public record ErrorResponse(
        int status,      // HTTP 상태 코드 (404, 400 ...)
        String message   // 사람이 읽을 수 있는 에러 설명
) {
}
