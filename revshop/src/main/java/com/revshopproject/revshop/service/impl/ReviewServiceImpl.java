package com.revshopproject.revshop.service.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.entity.*;
import com.revshopproject.revshop.repository.*;
import com.revshopproject.revshop.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LogManager.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final com.revshopproject.revshop.service.UserService userService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, 
                             ProductRepository productRepository, 
                             com.revshopproject.revshop.service.UserService userService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }
    
    
 // ADD this helper at the bottom of ReviewServiceImpl, before the closing brace:
    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Validation Error: Rating must be between 1 and 5.");
        }
    }
    

    @Override
    @Transactional
    public Review addReview(Long userId, Long productId, Review review) {
    	
    	validateRating(review.getRating());
        User user = userService.getCurrentUser();
        
        // 1. Optimized Purchase Check
        if (!reviewRepository.hasUserPurchasedProduct(user.getUserId(), productId)) {
            throw new RuntimeException("Unauthorized: You must receive this product before reviewing it.");
        }

        // 2. Prevent duplicate reviews
        if (reviewRepository.findByUser_UserIdAndProduct_ProductId(user.getUserId(), productId).isPresent()) {
            throw new RuntimeException("Review already exists. Use the update endpoint instead.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        review.setUser(user);
        review.setProduct(product);
        Review saved = reviewRepository.save(review);
        log.info("Review added: userId={}, productId={}, rating={}", user.getUserId(), productId, review.getRating());
        return saved;
    }

    @Override
    @Transactional
    public Review updateReview(Long productId, Review reviewDetails) {
    	
    	validateRating(reviewDetails.getRating());
        User user = userService.getCurrentUser();
        Review existingReview = reviewRepository.findByUser_UserIdAndProduct_ProductId(user.getUserId(), productId)
                .orElseThrow(() -> new RuntimeException("No existing review found to update."));

        existingReview.setRating(reviewDetails.getRating());
        existingReview.setCommentText(reviewDetails.getCommentText());

        Review saved = reviewRepository.save(existingReview);
        log.info("Review updated: userId={}, productId={}, newRating={}", user.getUserId(), productId, reviewDetails.getRating());
        return saved;
    }

    @Override
    @Transactional
    public void deleteReview(Long productId) {
        User user = userService.getCurrentUser();
        Review existingReview = reviewRepository.findByUser_UserIdAndProduct_ProductId(user.getUserId(), productId)
                .orElseThrow(() -> new RuntimeException("No existing review found to delete."));
        reviewRepository.delete(existingReview);
        log.info("Review deleted: userId={}, productId={}", user.getUserId(), productId);
    }

    @Override
    public java.util.Map<String, Object> getReviewStatus(Long productId) {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        try {
            User user = userService.getCurrentUser();
            boolean hasPurchased = reviewRepository.hasUserPurchasedProduct(user.getUserId(), productId);
            java.util.Optional<Review> existingReview = reviewRepository.findByUser_UserIdAndProduct_ProductId(user.getUserId(), productId);
            
            status.put("isAuthenticated", true);
            status.put("hasPurchased", hasPurchased);
            status.put("hasReviewed", existingReview.isPresent());
            status.put("review", existingReview.orElse(null));
        } catch (Exception e) {
            status.put("isAuthenticated", false);
            status.put("hasPurchased", false);
            status.put("hasReviewed", false);
        }
        return status;
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId);
    }
}