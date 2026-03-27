package com.demo.voucherservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/** Response trả về khi order-service gọi /api/vouchers/apply */
@Data
@Builder
public class ApplyVoucherResponse {
    private boolean valid;
    private String message;
    private BigDecimal discountAmount;
    private Long voucherId;
}
