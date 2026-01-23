package com.iuh.fit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity - Domain Partition
 * Tất cả các class liên quan đến User được đặt trong cùng một package
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String fullName;
}
