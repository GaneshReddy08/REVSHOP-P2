package com.revshopproject.revshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.CartItem;
import com.revshopproject.revshop.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_CartId(Long cartId);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    // Used by deleteProduct to clean up all cart references to a product
    List<CartItem> findByProduct_ProductId(Long productId);
}
