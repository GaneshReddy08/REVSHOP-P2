package com.revshopproject.revshop.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.entity.*;
import com.revshopproject.revshop.repository.*;
import com.revshopproject.revshop.service.CartService;
import com.revshopproject.revshop.service.UserService;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LogManager.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository, 
                           CartItemRepository cartItemRepository, 
                           ProductRepository productRepository,
                           UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        User currentUser = userService.getCurrentUser();
        // Ignore the passed userId and use the authenticated one for security
        return cartRepository.findByUser_UserId(currentUser.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    Cart saved = cartRepository.save(newCart);
                    log.info("New cart created for user ID: {}", currentUser.getUserId());
                    return saved;
                });
    }

    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        User currentUser = userService.getCurrentUser();
        Cart cart = getCartByUserId(currentUser.getUserId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return cartItemRepository.findByCartAndProduct(cart, product)
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    CartItem updated = cartItemRepository.save(existingItem);
                    log.info("Updated cart item quantity: productId={}, newQty={}", productId, updated.getQuantity());
                    return updated;
                })
                .orElseGet(() -> {
                    CartItem saved = cartItemRepository.save(new CartItem(null, cart, product, quantity));
                    log.info("Added new cart item: productId={}, qty={}", productId, quantity);
                    return saved;
                });
    }

    @Override
    @Transactional
    public CartItem updateItemQuantity(Long cartItemId, Integer quantity) {
        User currentUser = userService.getCurrentUser();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        
        // Ownership Check
        if (!item.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: This item does not belong to your cart.");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            log.info("Removed cart item ID: {} (quantity set to {})", cartItemId, quantity);
            return null;
        } else {
            item.setQuantity(quantity);
            CartItem saved = cartItemRepository.save(item);
            log.info("Updated cart item ID: {} to quantity: {}", cartItemId, quantity);
            return saved;
        }
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        User currentUser = userService.getCurrentUser();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        
        // Ownership Check
        if (!item.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: This item does not belong to your cart.");
        }
        
        cartItemRepository.delete(item);
        log.info("Removed cart item ID: {}", cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        User currentUser = userService.getCurrentUser();
        Cart cart = getCartByUserId(currentUser.getUserId());
        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());
        cartItemRepository.deleteAll(items);
        log.info("Cleared cart for user ID: {} ({} items removed)", currentUser.getUserId(), items.size());
    }
}