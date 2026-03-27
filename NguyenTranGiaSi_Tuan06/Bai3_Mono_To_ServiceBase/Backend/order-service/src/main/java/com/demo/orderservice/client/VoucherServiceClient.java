package com.demo.orderservice.client;

import com.demo.orderservice.client.dto.ApplyVoucherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class VoucherServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.voucher-url}")
    private String voucherServiceUrl;

    /**
     * Gọi voucher-service để apply voucher (validate + increment usage).
     * Trả về discount amount hoặc zero nếu không hợp lệ.
     */
    public ApplyVoucherDto applyVoucher(String code, BigDecimal orderTotal) {
        String url = UriComponentsBuilder
                .fromHttpUrl(voucherServiceUrl + "/api/vouchers/apply")
                .queryParam("code", code)
                .queryParam("orderTotal", orderTotal)
                .toUriString();
        return restTemplate.postForObject(url, null, ApplyVoucherDto.class);
    }
}
