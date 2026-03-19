package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.Review;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.ReviewRepository;
import com.revshopproject.revshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User currentUser;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(1L);

        product = new Product();
        product.setProductId(10L);

        review = new Review();
        review.setReviewId(100L);
        review.setRating(5);
        review.setCommentText("Great product!");
    }

    @Test
    void testAddReview_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reviewRepository.hasUserPurchasedProduct(1L, 10L)).thenReturn(true);
        when(reviewRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.empty());
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.addReview(1L, 10L, review);
        
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getCommentText());
    }

    @Test
    void testAddReview_NotPurchased() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reviewRepository.hasUserPurchasedProduct(1L, 10L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> reviewService.addReview(1L, 10L, review));
        assertEquals("Unauthorized: You must receive this product before reviewing it.", exception.getMessage());
    }

    @Test
    void testUpdateReview_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reviewRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review newDetails = new Review();
        newDetails.setRating(4);
        newDetails.setCommentText("Updated comment");

        Review result = reviewService.updateReview(10L, newDetails);
        
        assertEquals(4, result.getRating());
        assertEquals("Updated comment", result.getCommentText());
    }

    @Test
    void testDeleteReview_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(reviewRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(10L);
        verify(reviewRepository).delete(review);
    }
}
