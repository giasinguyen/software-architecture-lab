package com.demo.userservice.dto;

import com.demo.userservice.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private UserRole role;
}
