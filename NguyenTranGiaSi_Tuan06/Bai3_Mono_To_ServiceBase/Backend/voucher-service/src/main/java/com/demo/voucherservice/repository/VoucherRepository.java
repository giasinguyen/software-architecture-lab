package com.demo.voucherservice.repository;

import com.demo.voucherservice.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCodeAndActiveTrue(String code);
}
