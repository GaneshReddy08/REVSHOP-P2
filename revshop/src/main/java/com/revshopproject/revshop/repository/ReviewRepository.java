package com.revshopproject.revshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revshopproject.revshop.entity.Review;

import java.util.Optional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductId(Long productId);
    
    // Used to check if a user already reviewed this product (for updates)
    Optional<Review> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double getAverageRating(@Param("productId") Long productId);
 // Checks if a delivered order exists for this user and product
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
           "WHERE oi.order.user.userId = :userId " +
           "AND oi.product.productId = :productId " +
           "AND oi.order.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}