package com.demo.voucherservice.service;

import com.demo.voucherservice.dto.ApplyVoucherResponse;
import com.demo.voucherservice.dto.VoucherValidationResponse;
import com.demo.voucherservice.entity.Voucher;
import com.demo.voucherservice.enums.VoucherType;
import com.demo.voucherservice.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public Optional<Voucher> findValidVoucher(String code) {
        return voucherRepository.findByCodeAndActiveTrue(code)
                .filter(v -> v.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(v -> v.getUsedCount() < v.getMaxUsage());
    }

    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal) {
        if (voucher.getMinOrderAmount() != null
                && orderTotal.compareTo(voucher.getMinOrderAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        if (voucher.getType() == VoucherType.PERCENTAGE) {
            return orderTotal.multiply(voucher.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return voucher.getValue().min(orderTotal);
    }

    @Transactional(readOnly = true)
    public VoucherValidationResponse validate(String code, BigDecimal orderTotal) {
        Optional<Voucher> voucherOpt = findValidVoucher(code);
        if (voucherOpt.isEmpty()) {
            return VoucherValidationResponse.builder()
                    .valid(false)
                    .message("Voucher không hợp lệ hoặc đã hết hạn")
                    .discountAmount(BigDecimal.ZERO)
                    .finalAmount(orderTotal)
                    .build();
        }
        Voucher voucher = voucherOpt.get();
        BigDecimal discount = calculateDiscount(voucher, orderTotal);
        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            return VoucherValidationResponse.builder()
                    .valid(false)
                    .message("Đơn hàng chưa đạt giá trị tối thiểu " + voucher.getMinOrderAmount() + "đ")
                    .discountAmount(BigDecimal.ZERO)
                    .finalAmount(orderTotal)
                    .build();
        }
        return VoucherValidationResponse.builder()
                .valid(true)
                .message("Áp dụng voucher thành công!")
                .discountAmount(discount)
                .finalAmount(orderTotal.subtract(discount))
                .build();
    }

    /**
     * Được gọi bởi order-service khi đặt hàng:
     * validate + increment usage trong 1 lần gọi HTTP duy nhất.
     */
    @Transactional
    public ApplyVoucherResponse apply(String code, BigDecimal orderTotal) {
        Optional<Voucher> voucherOpt = findValidVoucher(code);
        if (voucherOpt.isEmpty()) {
            return ApplyVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher không hợp lệ hoặc đã hết hạn")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }
        Voucher voucher = voucherOpt.get();
        BigDecimal discount = calculateDiscount(voucher, orderTotal);
        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            return ApplyVoucherResponse.builder()
                    .valid(false)
                    .message("Đơn hàng chưa đạt giá trị tối thiểu " + voucher.getMinOrderAmount() + "đ")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
        return ApplyVoucherResponse.builder()
                .valid(true)
                .message("Áp dụng voucher thành công!")
                .discountAmount(discount)
                .voucherId(voucher.getId())
                .build();
    }
}
