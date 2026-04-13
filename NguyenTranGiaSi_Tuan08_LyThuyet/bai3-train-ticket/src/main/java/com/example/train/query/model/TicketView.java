package com.example.train.query.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_view")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TicketView {
    @Id
    private Long id;
    private String passengerName;
    private int seatNumber;
    private String status;
    private String trainNumber;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
}
