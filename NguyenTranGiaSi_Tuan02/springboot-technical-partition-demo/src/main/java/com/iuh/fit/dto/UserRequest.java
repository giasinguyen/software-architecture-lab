package com.iuh.fit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho User request - Technical Partition
 * Tất cả các DTO được đặt trong package dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String email;
    private String fullName;
}
