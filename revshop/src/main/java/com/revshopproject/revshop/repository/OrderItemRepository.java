package com.revshopproject.revshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderId(Long orderId);
    List<OrderItem> findByProduct_Seller_UserIdOrderByOrder_OrderIdDesc(Long sellerId);
    List<OrderItem> findByOrder_User_UserIdAndProduct_ProductId(Long userId, Long productId);
    // Used by deleteProduct to nullify references before deletion
    List<OrderItem> findByProduct_ProductId(Long productId);
}
