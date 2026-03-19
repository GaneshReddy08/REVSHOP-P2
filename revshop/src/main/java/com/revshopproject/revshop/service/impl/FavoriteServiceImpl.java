package com.revshopproject.revshop.service.impl;

import com.revshopproject.revshop.dto.ProductResponseDTO;
import com.revshopproject.revshop.entity.Favorite;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.FavoriteRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.UserRepository;
import com.revshopproject.revshop.service.FavoriteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger log = LogManager.getLogger(FavoriteServiceImpl.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long productId) {
        Optional<Favorite> existing = favoriteRepository.findByUser_UserIdAndProduct_ProductId(userId, productId);

        if (existing.isPresent()) {
            favoriteRepository.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
            log.info("Removed favorite: userId={}, productId={}", userId, productId);
        } else {
            Favorite favorite = new Favorite();
            // Using getReferenceById avoids a SELECT query since we only need the ID for the foreign key
            User user = userRepository.getReferenceById(userId);
            Product product = productRepository.getReferenceById(productId);

            favorite.setUser(user);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
            log.info("Added favorite: userId={}, productId={}", userId, productId);
        }
    }

    @Override
    public List<ProductResponseDTO> getFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUser_UserId(userId);
        log.debug("Fetched {} favorites for userId={}", favorites.size(), userId);
        return favorites.stream()
                .map(f -> convertToProductResponseDTO(f.getProduct()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isProductFavorited(Long userId, Long productId) {
        boolean result = favoriteRepository.findByUser_UserIdAndProduct_ProductId(userId, productId).isPresent();
        log.debug("isProductFavorited: userId={}, productId={} -> {}", userId, productId, result);
        return result;
    }

    @Override
    @Transactional
    public void clearAllFavorites(Long userId) {
        List<Favorite> userFavorites = favoriteRepository.findByUser_UserId(userId);
        favoriteRepository.deleteAll(userFavorites);
        log.info("Cleared all favorites for userId={} ({} removed)", userId, userFavorites.size());
    }

    // Reuse your existing mapping logic
    private ProductResponseDTO convertToProductResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setMrp(product.getMrp());
        dto.setDescription(product.getDescription());
        
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSeller() != null) {
            dto.setSellerBusinessName(product.getSeller().getBusinessName());
        }
        
        dto.setPrimaryImageUrl(product.getPrimaryImageUrl());
        dto.setFavorited(true);
        return dto;
    }
}