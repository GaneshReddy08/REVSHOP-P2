package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revshopproject.revshop.dto.ProductResponseDTO;
import com.revshopproject.revshop.entity.Favorite;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.FavoriteRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void testToggleFavorite_Add() {
        when(favoriteRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(new User());
        when(productRepository.getReferenceById(10L)).thenReturn(new Product());

        favoriteService.toggleFavorite(1L, 10L);
        
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testToggleFavorite_Remove() {
        Favorite existing = new Favorite();
        when(favoriteRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.of(existing));

        favoriteService.toggleFavorite(1L, 10L);
        
        verify(favoriteRepository).deleteByUser_UserIdAndProduct_ProductId(1L, 10L);
    }

    @Test
    void testGetFavoritesByUserId() {
        List<Favorite> favorites = new ArrayList<>();
        Favorite fav = new Favorite();
        Product product = new Product();
        product.setProductId(10L);
        product.setName("Fav Product");
        fav.setProduct(product);
        favorites.add(fav);

        when(favoriteRepository.findByUser_UserId(1L)).thenReturn(favorites);

        List<ProductResponseDTO> result = favoriteService.getFavoritesByUserId(1L);
        
        assertEquals(1, result.size());
        assertEquals("Fav Product", result.get(0).getName());
        assertTrue(result.get(0).isFavorited());
    }

    @Test
    void testIsProductFavorited() {
        when(favoriteRepository.findByUser_UserIdAndProduct_ProductId(1L, 10L)).thenReturn(Optional.of(new Favorite()));
        assertTrue(favoriteService.isProductFavorited(1L, 10L));
    }
}
