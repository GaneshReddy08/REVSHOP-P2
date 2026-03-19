package com.revshopproject.revshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.revshopproject.revshop.dto.ProductRequestDTO;
import com.revshopproject.revshop.dto.ProductResponseDTO;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.service.ProductService;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.ProductImageRepository;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductController productController;

    @Test
    void testGetAllProducts() {
        List<Product> mockProducts = new ArrayList<>();
        Product p = new Product();
        // Set essential fields to pass through fromEntity
        p.setProductId(1L);
        p.setName("Test Product");
        mockProducts.add(p);

        when(productService.getAllProducts()).thenReturn(mockProducts);

        List<ProductResponseDTO> result = productController.getAll();

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testAddProduct() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("New Product");

        Product savedProduct = new Product();
        savedProduct.setProductId(10L);
        savedProduct.setName("New Product");

        when(productService.saveProduct(any(ProductRequestDTO.class))).thenReturn(savedProduct);

        ResponseEntity<ProductResponseDTO> response = productController.addProduct(dto);

        // Instead of getStatusCodeValue(), use getStatusCode().value() for newer Spring 6/Boot 3 if applicable,
        // else getStatusCodeValue() works in Boot 2/3 but is deprecated in 3.
        // Assuming Spring Boot 3 since tests utilize JUnit Jupiter heavily
        assertEquals(200, response.getStatusCode().value());
        assertEquals("New Product", response.getBody().getName());
    }
}
