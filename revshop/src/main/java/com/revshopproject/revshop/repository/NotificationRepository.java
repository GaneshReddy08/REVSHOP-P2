package com.revshopproject.revshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revshopproject.revshop.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. Existing: Fetch all for a user (Newest first)
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 2. Existing: Fetch specific status (0 for unread, 1 for read)
    List<Notification> findByUser_UserIdAndIsReadOrderByCreatedAtDesc(Long userId, Integer isRead);

    // 3. NEW: High-performance count for the UI notification badge
    long countByUser_UserIdAndIsRead(Long userId, Integer isRead);

    // 4. NEW: Bulk update to clear all notifications at once
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.user.userId = :userId AND n.isRead = 0")
    void markAllAsRead(@Param("userId") Long userId);
    
    
}