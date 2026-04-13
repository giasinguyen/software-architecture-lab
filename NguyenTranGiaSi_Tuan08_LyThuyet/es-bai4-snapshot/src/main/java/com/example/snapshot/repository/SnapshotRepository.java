package com.example.snapshot.repository;

import com.example.snapshot.model.AccountSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SnapshotRepository extends JpaRepository<AccountSnapshot, Long> {
    Optional<AccountSnapshot> findTopByAccountIdOrderByIdDesc(String accountId);
}
