package com.revshopproject.revshop.service;

import java.util.List;

import com.revshopproject.revshop.dto.ProductResponseDTO;

public interface FavoriteService {

	// Core logic: Add if missing, remove if present
	void toggleFavorite(Long userId, Long productId);

	// Get the full list of favorited products for the user's wishlist page
	List<ProductResponseDTO> getFavoritesByUserId(Long userId);

	// Helper for the UI: Check if a specific product should show a red heart
	boolean isProductFavorited(Long userId, Long productId);

	// Clear all favorites (e.g., user wants to reset their wishlist)
	void clearAllFavorites(Long userId);
}