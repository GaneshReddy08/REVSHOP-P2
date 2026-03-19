package com.revshopproject.revshop.service;

import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.CartItem;

public interface CartService {
    Cart getCartByUserId(Long userId);
    CartItem addItemToCart(Long userId, Long productId, Integer quantity);
    void removeItemFromCart(Long cartItemId);
    CartItem updateItemQuantity(Long cartItemId, Integer quantity);
    void clearCart(Long userId);
}