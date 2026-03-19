package com.revshopproject.revshop.service;

import java.util.List;

import com.revshopproject.revshop.entity.Notification;
import com.revshopproject.revshop.entity.User;

public interface NotificationService {
    void sendNotification(User user, String message);
    List<Notification> getNotificationsForUser(Long userId);
    List<Notification> getUnreadNotificationsForUser(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);   
    long getUnreadCount(Long userId); 
}