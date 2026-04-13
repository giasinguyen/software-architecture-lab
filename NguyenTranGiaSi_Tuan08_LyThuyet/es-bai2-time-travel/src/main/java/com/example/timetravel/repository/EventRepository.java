package com.example.timetravel.repository;

import com.example.timetravel.event.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<AccountEvent, Long> {
    List<AccountEvent> findByAccountIdOrderByIdAsc(String accountId);
}
