package com.demo.vertical.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Vertical partition – chỉ giữ các cột "hot" (truy cập thường xuyên).
 * I/O mỗi query nhỏ hơn → nhanh hơn khi so với user_full.
 */
@Entity
@Table(name = "user_basic")
@Data
public class UserBasic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
