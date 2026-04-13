package com.example.snapshot.repository;

import com.example.snapshot.model.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<AccountEvent, Long> {
    List<AccountEvent> findByAccountIdOrderByIdAsc(String accountId);
    List<AccountEvent> findByAccountIdAndIdGreaterThanOrderByIdAsc(String accountId, Long afterId);
    long countByAccountId(String accountId);
}
