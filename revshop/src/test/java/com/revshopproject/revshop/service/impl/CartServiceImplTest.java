package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.CartItem;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartItemRepository;
import com.revshopproject.revshop.repository.CartRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User currentUser;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(1L);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(currentUser);

        product = new Product();
        product.setProductId(10L);
    }

    @Test
    void testGetCartByUserId() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUserId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getCartId());
    }

    @Test
    void testAddItemToCart_NewItem() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        CartItem savedItem = new CartItem(1L, cart, product, 2);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);

        CartItem result = cartService.addItemToCart(1L, 10L, 2);
        assertEquals(2, result.getQuantity());
        assertEquals(10L, result.getProduct().getProductId());
    }

    @Test
    void testUpdateItemQuantity_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        CartItem item = new CartItem(100L, cart, product, 1);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(item)).thenReturn(item);

        CartItem result = cartService.updateItemQuantity(100L, 5);
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
    }

    @Test
    void testUpdateItemQuantity_Unauthorized() {
        User otherUser = new User();
        otherUser.setUserId(99L); // Diff user
        
        Cart otherCart = new Cart();
        otherCart.setUser(otherUser);
        
        CartItem item = new CartItem(100L, otherCart, product, 5);
        
        // Logged in as User 1
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> cartService.updateItemQuantity(100L, 2));
    }
}
