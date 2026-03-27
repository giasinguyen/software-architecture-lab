package com.demo.notificationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private Long orderId;
    private boolean read;
    private LocalDateTime createdAt;
}
