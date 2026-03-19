package com.revshopproject.revshop.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.entity.Notification;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.NotificationRepository;
import com.revshopproject.revshop.service.NotificationService;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LogManager.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void sendNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
        log.info("Notification sent to userId={}: {}", user.getUserId(), message);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        log.debug("Fetched {} notifications for userId={}", notifications.size(), userId);
        return notifications;
    }

    @Override
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        List<Notification> unread = notificationRepository.findByUser_UserIdAndIsReadOrderByCreatedAtDesc(userId, 0);
        log.debug("Fetched {} unread notifications for userId={}", unread.size(), userId);
        return unread;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(1);
            notificationRepository.save(n);
            log.info("Marked notification ID: {} as read", notificationId);
        });
    }

//    @Override
//    @Transactional
//    public void markAllAsRead(Long userId) {
//        List<Notification> unread = notificationRepository.findByUser_UserIdAndIsReadOrderByCreatedAtDesc(userId, 0);
//        unread.forEach(n -> n.setIsRead(1));
//        notificationRepository.saveAll(unread);
//    }
    
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
        log.info("Marked all notifications as read for userId={}", userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        long count = notificationRepository.countByUser_UserIdAndIsRead(userId, 0);
        log.debug("Unread notification count for userId={}: {}", userId, count);
        return count;
    }
}