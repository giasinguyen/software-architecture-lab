package com.demo.vertical.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Bảng CHƯA tách – dùng để so sánh benchmark.
 * Có đầy đủ 8 cột bao gồm cả TEXT và JSON (tốn I/O hơn).
 */
@Entity
@Table(name = "user_full")
@Data
public class UserFull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(columnDefinition = "json")
    private String settings;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
