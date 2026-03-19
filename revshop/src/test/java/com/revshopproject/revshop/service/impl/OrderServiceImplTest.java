package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.dto.OrderRequestDTO;
import com.revshopproject.revshop.dto.OrderResponseDTO;
import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.CartItem;
import com.revshopproject.revshop.entity.Order;
import com.revshopproject.revshop.entity.OrderItem;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartItemRepository;
import com.revshopproject.revshop.repository.CartRepository;
import com.revshopproject.revshop.repository.OrderItemRepository;
import com.revshopproject.revshop.repository.OrderRepository;
import com.revshopproject.revshop.repository.PaymentRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.service.NotificationService;
import com.revshopproject.revshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private NotificationService notificationService;
    @Mock private UserService userService;
    @Mock private PaymentRepository paymentRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User currentUser;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(1L);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(currentUser);

        product = new Product();
        product.setProductId(10L);
        product.setName("Prod1");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(50);
        product.setSeller(new User());

        cartItem = new CartItem(1L, cart, product, 2);
    }

    @Test
    void testPlaceOrder_Success() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setPaymentMethod("CREDIT_CARD");
        dto.setShippingAddress("123 Street");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart));
        
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        when(cartItemRepository.findByCart_CartId(1L)).thenReturn(cartItems);

        Order savedOrder = new Order();
        savedOrder.setOrderId(100L);
        savedOrder.setStatus("PENDING");
        savedOrder.setPaymentMethod("CREDIT_CARD");

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());

        OrderResponseDTO result = orderService.placeOrder(dto);
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setPaymentMethod("UPI");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_CartId(1L)).thenReturn(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.placeOrder(dto));
        assertEquals("Cart is empty!", exception.getMessage());
    }
}
