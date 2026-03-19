package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.CartItem;
import com.revshopproject.revshop.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET: http://localhost:8888/api/cart/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<com.revshopproject.revshop.dto.CartResponseDTO> getCart(@PathVariable Long userId) {
        // userId ignored in favor of authenticated context for security
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(com.revshopproject.revshop.dto.CartResponseDTO.fromEntity(cart));
    }

    // POST: http://localhost:8888/api/cart/add?productId=5&quantity=2
    @PostMapping("/add")
    public ResponseEntity<com.revshopproject.revshop.dto.CartItemResponseDTO> addToCart(
            @RequestParam Long productId, 
            @RequestParam Integer quantity) {
        // userId is now retrieved from the authenticated context in the service
        CartItem cartItem = cartService.addItemToCart(null, productId, quantity);
        return ResponseEntity.ok(com.revshopproject.revshop.dto.CartItemResponseDTO.fromEntity(cartItem));
    }

    // DELETE: http://localhost:8888/api/cart/item/10
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    // PATCH: http://localhost:8888/api/cart/item/10?quantity=5
    @PatchMapping("/item/{cartItemId}")
    public ResponseEntity<com.revshopproject.revshop.dto.CartItemResponseDTO> updateItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartItem updatedItem = cartService.updateItemQuantity(cartItemId, quantity);
        if (updatedItem == null) {
            return ResponseEntity.noContent().build(); // Item was removed
        }
        return ResponseEntity.ok(com.revshopproject.revshop.dto.CartItemResponseDTO.fromEntity(updatedItem));
    }

    // DELETE: http://localhost:8888/api/cart/user/1/clear
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        // userId ignored in favor of authenticated context for security
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}