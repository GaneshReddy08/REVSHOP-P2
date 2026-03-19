package com.revshopproject.revshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.service.CartService;

import com.revshopproject.revshop.dto.CartResponseDTO;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void testGetCart() {
        Cart cart = new Cart();
        // Setup minimal mock cart
        cart.setCartId(1L);

        when(cartService.getCartByUserId(1L)).thenReturn(cart);

        ResponseEntity<CartResponseDTO> response = cartController.getCart(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getCartId());
    }
}
