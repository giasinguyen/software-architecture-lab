package com.example.train.command.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookTicketRequest {
    private Long tripId;
    private String passengerName;
    private int seatNumber;
}
