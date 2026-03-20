package com.iuh.fit.dto.response;

import com.iuh.fit.enums.UserRole;
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
