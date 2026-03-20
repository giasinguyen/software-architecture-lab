package com.iuh.fit.controller;

import com.iuh.fit.dto.response.VoucherValidationResponse;
import com.iuh.fit.service.VoucherService;
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

    @GetMapping("/validate")
    public ResponseEntity<VoucherValidationResponse> validate(
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(voucherService.validate(code, orderTotal));
    }
}
