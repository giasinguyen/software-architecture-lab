package com.demo.horizontal.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String gender; // "M" or "F"
}
