package com.example.ecommerce.service;

import com.example.ecommerce.domain.CartItem;
import com.example.ecommerce.domain.Member;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CartItemAddRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.global.exception.CartItemNotFoundException;
import com.example.ecommerce.global.exception.ForbiddenException;
import com.example.ecommerce.global.exception.MemberNotFoundException;
import com.example.ecommerce.global.exception.ProductNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.MemberRepository;
import com.example.ecommerce.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                       MemberRepository memberRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    // 장바구니 담기. 이미 담긴 상품이면 수량을 더한다.
    @Transactional
    public CartItemResponse addToCart(Long memberId, CartItemAddRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        CartItem cartItem = cartItemRepository.findByMemberAndProduct(member, product)
                .map(existing -> {
                    // 이미 있으면 수량만 증가. (변경 감지(dirty checking)로 트랜잭션 끝에 자동 UPDATE)
                    existing.addQuantity(request.quantity());
                    return existing;
                })
                .orElseGet(() ->
                        // 없으면 새로 저장
                        cartItemRepository.save(CartItem.create(member, product, request.quantity())));

        return CartItemResponse.from(cartItem);
    }

    // 내 장바구니 조회
    public List<CartItemResponse> getCart(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return cartItemRepository.findByMember(member).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    // 장바구니 항목 삭제. 반드시 "본인 것"인지 검증한다.
    @Transactional
    public void deleteCartItem(Long memberId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        // 소유자 검증: 남의 장바구니 항목을 지우지 못하게 막는다.
        if (!cartItem.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("본인의 장바구니 항목만 삭제할 수 있습니다.");
        }

        cartItemRepository.delete(cartItem);
    }
}
