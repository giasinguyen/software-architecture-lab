package com.example.train.query.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_view")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TripView {
    @Id
    private Long id;
    private String trainNumber;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int availableSeats;
}
