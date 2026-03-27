package com.demo.voucherservice.controller;

import com.demo.voucherservice.dto.ApplyVoucherResponse;
import com.demo.voucherservice.dto.VoucherValidationResponse;
import com.demo.voucherservice.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // Frontend gọi: kiểm tra voucher mà không consume
    @GetMapping("/validate")
    public ResponseEntity<VoucherValidationResponse> validate(
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(voucherService.validate(code, orderTotal));
    }

    // order-service gọi: validate + increment usage
    @PostMapping("/apply")
    public ResponseEntity<ApplyVoucherResponse> apply(
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(voucherService.apply(code, orderTotal));
    }
}
