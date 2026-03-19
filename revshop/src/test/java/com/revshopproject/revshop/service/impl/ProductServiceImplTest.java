package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.dto.ProductRequestDTO;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartItemRepository;
import com.revshopproject.revshop.repository.CategoryRepository;
import com.revshopproject.revshop.repository.FavoriteRepository;
import com.revshopproject.revshop.repository.OrderItemRepository;
import com.revshopproject.revshop.repository.ProductImageRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.ReviewRepository;
import com.revshopproject.revshop.service.NotificationService;
import com.revshopproject.revshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setUserId(1L);
        seller.setRole("SELLER");
    }

    @Test
    void testSaveProduct_Success() {
        when(userService.getCurrentUser()).thenReturn(seller);
        
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("New Prod");
        dto.setPrice(new BigDecimal("90.00"));
        dto.setMrp(new BigDecimal("100.00"));
        dto.setStock(50);
        dto.setInventoryThreshold(10);
        
        Product savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setName("New Prod");
        savedProduct.setStock(50);
        savedProduct.setInventoryThreshold(10);
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.saveProduct(dto);
        assertEquals("New Prod", result.getName());
    }

    @Test
    void testSaveProduct_AccessDenied() {
        User buyer = new User();
        buyer.setRole("BUYER");
        when(userService.getCurrentUser()).thenReturn(buyer);

        ProductRequestDTO dto = new ProductRequestDTO();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.saveProduct(dto));
        assertEquals("Access Denied: Only users with the 'SELLER' role can add products.", exception.getMessage());
    }

    @Test
    void testSaveProduct_PriceValidation() {
        when(userService.getCurrentUser()).thenReturn(seller);
        
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setPrice(new BigDecimal("110.00"));
        dto.setMrp(new BigDecimal("100.00")); // Price > MRP
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.saveProduct(dto));
        assertEquals("Validation Error: Selling price cannot be higher than the MRP.", exception.getMessage());
    }

    @Test
    void testDeleteProduct_Success() {
        when(userService.getCurrentUser()).thenReturn(seller);
        
        Product product = new Product();
        product.setProductId(10L);
        product.setSeller(seller);

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByProduct_ProductId(10L)).thenReturn(Collections.emptyList());
        when(favoriteRepository.findByProduct_ProductId(10L)).thenReturn(Collections.emptyList());
        when(orderItemRepository.findByProduct_ProductId(10L)).thenReturn(Collections.emptyList());
        when(reviewRepository.findByProduct_ProductId(10L)).thenReturn(Collections.emptyList());

        productService.deleteProduct(10L);
        verify(productRepository).delete(product);
    }
}
