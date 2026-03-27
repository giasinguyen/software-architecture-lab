package com.demo.orderservice.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/** DTO nhận từ food-service GET /api/foods/{id} */
@Data
public class FoodDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private boolean available;
    private int orderCount;
}
