package com.example.train.command.service;

import com.example.train.command.dto.BookTicketRequest;
import com.example.train.command.model.*;
import com.example.train.command.repository.TicketWriteRepository;
import com.example.train.command.repository.TripRepository;
import com.example.train.query.model.TicketView;
import com.example.train.query.repository.TicketReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketCommandService {

    private final TicketWriteRepository ticketWriteRepository;
    private final TripRepository tripRepository;
    private final TicketReadRepository ticketReadRepository;

    @Transactional
    public TicketEntity bookTicket(BookTicketRequest request) {
        TripEntity trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found: " + request.getTripId()));

        // Validate seat availability
        if (trip.getAvailableSeats() <= 0) {
            throw new RuntimeException("No available seats for trip: " + trip.getTrainNumber());
        }
        if (request.getSeatNumber() < 1 || request.getSeatNumber() > trip.getTotalSeats()) {
            throw new RuntimeException("Invalid seat number: " + request.getSeatNumber());
        }
        boolean seatTaken = ticketWriteRepository.existsByTripIdAndSeatNumberAndStatus(
                trip.getId(), request.getSeatNumber(), TicketStatus.BOOKED);
        if (seatTaken) {
            throw new RuntimeException("Seat " + request.getSeatNumber() + " is already booked");
        }

        // Book ticket
        TicketEntity ticket = TicketEntity.builder()
                .passengerName(request.getPassengerName())
                .seatNumber(request.getSeatNumber())
                .status(TicketStatus.BOOKED)
                .trip(trip)
                .bookedAt(LocalDateTime.now())
                .build();
        ticket = ticketWriteRepository.save(ticket);

        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepository.save(trip);

        // Sync read model
        ticketReadRepository.save(new TicketView(ticket.getId(), ticket.getPassengerName(),
                ticket.getSeatNumber(), ticket.getStatus().name(),
                trip.getTrainNumber(), trip.getDeparture(), trip.getDestination(),
                trip.getDepartureTime()));
        return ticket;
    }

    @Transactional
    public TicketEntity cancelTicket(Long ticketId) {
        TicketEntity ticket = ticketWriteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
        ticket.setStatus(TicketStatus.CANCELLED);
        ticket = ticketWriteRepository.save(ticket);

        TripEntity trip = ticket.getTrip();
        trip.setAvailableSeats(trip.getAvailableSeats() + 1);
        tripRepository.save(trip);

        // Update read model
        ticketReadRepository.deleteById(ticketId);
        return ticket;
    }
}
