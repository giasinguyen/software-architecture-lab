package com.demo.horizontal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_female")
@Data
public class UserFemale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String gender = "F";

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
