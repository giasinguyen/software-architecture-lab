package com.demo.horizontal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_male")
@Data
public class UserMale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String gender = "M";

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
