package com.iuh.fit.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFoodRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
}
