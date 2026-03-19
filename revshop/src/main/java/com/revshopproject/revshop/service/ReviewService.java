package com.revshopproject.revshop.service;

import java.util.List;

import com.revshopproject.revshop.entity.Review;

public interface ReviewService {
    Review addReview(Long userId, Long productId, Review review);
    List<Review> getReviewsByProduct(Long productId);
    
    //method for updating existing ratings/comments
    Review updateReview(Long productId, Review reviewDetails);
    
    void deleteReview(Long productId);
    
    java.util.Map<String, Object> getReviewStatus(Long productId);
}