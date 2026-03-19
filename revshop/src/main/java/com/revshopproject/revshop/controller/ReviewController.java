package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.dto.ReviewRequestDTO;
import com.revshopproject.revshop.entity.Review;
import com.revshopproject.revshop.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST: Add new review (Validates purchase)
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequestDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setCommentText(dto.getComment());
        return ResponseEntity.ok(reviewService.addReview(null, dto.getProductId(), review)); // userId handled by service
    }

    // PUT: Update existing review
    @PutMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestBody ReviewRequestDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setCommentText(dto.getComment());
        return ResponseEntity.ok(reviewService.updateReview(dto.getProductId(), review));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long productId) {
        reviewService.deleteReview(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{productId}")
    public ResponseEntity<java.util.Map<String, Object>> getReviewStatus(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewStatus(productId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}