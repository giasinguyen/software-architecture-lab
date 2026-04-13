package com.example.train.command.repository;

import com.example.train.command.model.TicketEntity;
import com.example.train.command.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketWriteRepository extends JpaRepository<TicketEntity, Long> {
    boolean existsByTripIdAndSeatNumberAndStatus(Long tripId, int seatNumber, TicketStatus status);
}
