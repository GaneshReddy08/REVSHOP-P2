package com.revshopproject.revshop.repository;

import com.revshopproject.revshop.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_UserId(Long userId);
    Optional<Favorite> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    // Used by deleteProduct to clean up all favorites referencing this product
    List<Favorite> findByProduct_ProductId(Long productId);
}
