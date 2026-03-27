package com.demo.vertical.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Vertical partition – cột "cold" (ít truy cập): avatar, bio.
 * id là FK tới user_basic.id, không tự sinh.
 */
@Entity
@Table(name = "user_profile")
@Data
public class UserProfile {

    @Id
    private Long id;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;
}
