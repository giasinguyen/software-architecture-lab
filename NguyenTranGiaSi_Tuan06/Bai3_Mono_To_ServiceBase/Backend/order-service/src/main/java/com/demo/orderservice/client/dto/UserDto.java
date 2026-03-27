package com.demo.orderservice.client.dto;

import lombok.Data;

/** DTO nhận từ user-service GET /api/users/{id} */
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
}
