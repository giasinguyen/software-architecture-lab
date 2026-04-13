package com.example.train.query.repository;

import com.example.train.query.model.TicketView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketReadRepository extends JpaRepository<TicketView, Long> {
}
