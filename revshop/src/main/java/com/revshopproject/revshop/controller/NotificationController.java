package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.entity.Notification;
import com.revshopproject.revshop.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET: http://localhost:8888/api/notifications/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    // GET: http://localhost:8888/api/notifications/user/1/unread-count
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        // You'll need to add this method to your Service Implementation first
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    // PATCH: http://localhost:8888/api/notifications/1/read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
 // DELETE or PATCH: http://localhost:8888/api/notifications/user/1/clear-all
    @PatchMapping("/user/{userId}/clear-all")
    public ResponseEntity<Void> clearAllNotifications(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}