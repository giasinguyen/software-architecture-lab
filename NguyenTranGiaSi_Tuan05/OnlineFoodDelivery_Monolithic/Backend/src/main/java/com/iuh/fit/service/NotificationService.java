package com.iuh.fit.service;

import com.iuh.fit.dto.response.NotificationResponse;
import com.iuh.fit.entity.Notification;
import com.iuh.fit.entity.User;
import com.iuh.fit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void send(User user, String message, Long orderId) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .orderId(orderId)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> unread =
                notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .orderId(n.getOrderId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
