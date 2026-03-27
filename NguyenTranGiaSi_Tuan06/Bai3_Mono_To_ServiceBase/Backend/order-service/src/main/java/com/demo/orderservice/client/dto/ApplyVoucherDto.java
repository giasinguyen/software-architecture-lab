package com.demo.orderservice.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/** DTO nhận từ voucher-service POST /api/vouchers/apply */
@Data
public class ApplyVoucherDto {
    private boolean valid;
    private String message;
    private BigDecimal discountAmount;
    private Long voucherId;
}
