package com.revshopproject.revshop.controller;

import com.revshopproject.revshop.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Toggles the favorite status of a product for the logged-in user.
     * URL: POST /api/favorites/toggle/{productId}
     */
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long productId, HttpSession session) {
        // 1. Get the userId from the session (Security Check)
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please login to favorite products"));
        }

        try {
            // 2. Execute the toggle logic
            favoriteService.toggleFavorite(userId, productId);
            
            // 3. Check new status to send back to frontend
            boolean isFavorited = favoriteService.isProductFavorited(userId, productId);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "isFavorited", isFavorited,
                "message", isFavorited ? "💖 Added to wishlist" : "💔 Removed from wishlist"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Error updating favorite"));
        }
    }

    /**
     * Helper endpoint to check if a product is favorited (useful for dynamic UI loading).
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long productId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.ok(false);
        
        return ResponseEntity.ok(favoriteService.isProductFavorited(userId, productId));
    }

    /**
     * Fetch all favorites for the currently logged-in user.
     */
    @GetMapping("/mine")
    public ResponseEntity<?> getMyFavorites(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please login to view favorites"));
        }
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }
}