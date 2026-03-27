package com.demo.vertical.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Vertical partition – cột "cold" (ít truy cập): last_login, settings.
 * id là FK tới user_basic.id, không tự sinh.
 */
@Entity
@Table(name = "user_activity")
@Data
public class UserActivity {

    @Id
    private Long id;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(columnDefinition = "json")
    private String settings;
}
