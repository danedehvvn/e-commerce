package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartItemAddRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 장바구니 API. 모두 인증 필요(SecurityConfig의 anyRequest().authenticated()에 걸림).
// @AuthenticationPrincipal로 현재 로그인 회원 id를 꺼내 쓴다(3단계에서 만든 것).
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // POST /api/cart — 담기
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addToCart(@AuthenticationPrincipal Long memberId,
                                      @Valid @RequestBody CartItemAddRequest request) {
        return cartService.addToCart(memberId, request);
    }

    // GET /api/cart — 내 장바구니
    @GetMapping
    public List<CartItemResponse> getCart(@AuthenticationPrincipal Long memberId) {
        return cartService.getCart(memberId);
    }

    // DELETE /api/cart/{cartItemId} — 항목 삭제 (성공 시 204 No Content)
    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@AuthenticationPrincipal Long memberId,
                               @PathVariable Long cartItemId) {
        cartService.deleteCartItem(memberId, cartItemId);
    }
}
