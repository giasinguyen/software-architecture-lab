package com.example.bank.repository;

import com.example.bank.event.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<AccountEvent, Long> {
    List<AccountEvent> findByAccountIdOrderByIdAsc(String accountId);
}
