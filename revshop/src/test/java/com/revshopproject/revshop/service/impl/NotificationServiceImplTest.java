package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.revshopproject.revshop.entity.Notification;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testSendNotification() {
        User user = new User();
        user.setUserId(1L);

        notificationService.sendNotification(user, "Test Message");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetNotificationsForUser() {
        List<Notification> list = new ArrayList<>();
        list.add(new Notification());
        
        when(notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(1L)).thenReturn(list);
        
        List<Notification> result = notificationService.getNotificationsForUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testMarkAsRead() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setIsRead(0);
        
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        
        notificationService.markAsRead(1L);
        
        assertEquals(1, notification.getIsRead());
        verify(notificationRepository).save(notification);
    }
}
