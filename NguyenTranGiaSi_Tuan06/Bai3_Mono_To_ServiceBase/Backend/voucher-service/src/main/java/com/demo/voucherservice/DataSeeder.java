package com.demo.voucherservice;

import com.demo.voucherservice.entity.Voucher;
import com.demo.voucherservice.enums.VoucherType;
import com.demo.voucherservice.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final VoucherRepository voucherRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (voucherRepository.count() == 0) {
            LocalDateTime expiry = LocalDateTime.now().plusDays(30);
            voucherRepository.saveAll(List.of(
                    Voucher.builder().code("WELCOME20").type(VoucherType.PERCENTAGE).value(BigDecimal.valueOf(20)).minOrderAmount(BigDecimal.valueOf(50000)).maxUsage(100).usedCount(0).expiresAt(expiry).active(true).build(),
                    Voucher.builder().code("FREESHIP").type(VoucherType.FIXED).value(BigDecimal.valueOf(15000)).minOrderAmount(BigDecimal.valueOf(30000)).maxUsage(50).usedCount(0).expiresAt(expiry).active(true).build(),
                    Voucher.builder().code("SALE50K").type(VoucherType.FIXED).value(BigDecimal.valueOf(50000)).minOrderAmount(BigDecimal.valueOf(200000)).maxUsage(20).usedCount(0).expiresAt(expiry).active(true).build()
            ));
        }
    }
}
