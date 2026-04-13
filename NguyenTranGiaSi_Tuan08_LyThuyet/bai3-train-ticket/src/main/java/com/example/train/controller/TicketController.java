package com.example.train.controller;

import com.example.train.command.dto.BookTicketRequest;
import com.example.train.command.model.TicketEntity;
import com.example.train.command.service.TicketCommandService;
import com.example.train.query.model.TicketView;
import com.example.train.query.model.TripView;
import com.example.train.query.service.TicketViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketCommandService commandService;
    private final TicketViewService viewService;

    // ===== COMMAND =====

    @PostMapping("/tickets/book")
    public ResponseEntity<TicketEntity> book(@RequestBody BookTicketRequest request) {
        return ResponseEntity.ok(commandService.bookTicket(request));
    }

    @PutMapping("/tickets/{id}/cancel")
    public ResponseEntity<TicketEntity> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.cancelTicket(id));
    }

    // ===== QUERY =====

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketView>> getTickets() {
        return ResponseEntity.ok(viewService.getAllTickets());
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripView>> getTrips() {
        return ResponseEntity.ok(viewService.getAllTrips());
    }

    @GetMapping("/trips/search")
    public ResponseEntity<List<TripView>> searchTrips(
            @RequestParam String departure, @RequestParam String destination) {
        return ResponseEntity.ok(viewService.searchTrips(departure, destination));
    }
}
