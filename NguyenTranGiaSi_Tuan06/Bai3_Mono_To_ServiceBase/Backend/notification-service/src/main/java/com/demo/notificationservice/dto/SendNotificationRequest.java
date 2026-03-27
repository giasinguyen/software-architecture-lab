package com.demo.notificationservice.dto;

import lombok.Data;

/** Payload nhận từ order-service để tạo notification mới */
@Data
public class SendNotificationRequest {
    private Long userId;
    private String message;
    private Long orderId;
}
