package com.demo.notificationservice.controller;

import com.demo.notificationservice.dto.NotificationResponse;
import com.demo.notificationservice.dto.SendNotificationRequest;
import com.demo.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // order-service gọi để tạo notification
    @PostMapping
    public ResponseEntity<Void> send(@RequestBody SendNotificationRequest request) {
        notificationService.send(request);
        return ResponseEntity.ok().build();
    }

    // Frontend gọi để lấy notifications của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }
}
