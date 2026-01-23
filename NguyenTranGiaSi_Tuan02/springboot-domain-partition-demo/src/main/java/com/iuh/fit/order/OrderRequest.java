package com.iuh.fit.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho Order request - Domain Partition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long userId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
}
