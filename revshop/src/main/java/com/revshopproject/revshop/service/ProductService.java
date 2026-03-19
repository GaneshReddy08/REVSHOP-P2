package com.revshopproject.revshop.service;

import java.util.List;
import java.util.Optional;

import com.revshopproject.revshop.dto.ProductRequestDTO;
import com.revshopproject.revshop.entity.Product;

public interface ProductService {
    Product saveProduct(ProductRequestDTO dto);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(Long categoryId);
    void deleteProduct(Long id);
    List<Product> searchProducts(String keyword);
    List<Product> getProductsBySellerId(Long sellerId);
    void addImageToProduct(Long productId, String imageUrl);
    Product updateProduct(Long id, ProductRequestDTO dto);
}