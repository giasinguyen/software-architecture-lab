package com.example.train.query.service;

import com.example.train.query.model.TicketView;
import com.example.train.query.model.TripView;
import com.example.train.query.repository.TicketReadRepository;
import com.example.train.query.repository.TripViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketViewService {

    private final TicketReadRepository ticketReadRepository;
    private final TripViewRepository tripViewRepository;

    public List<TicketView> getAllTickets() {
        return ticketReadRepository.findAll();
    }

    public List<TripView> searchTrips(String departure, String destination) {
        return tripViewRepository.findByDepartureAndDestination(departure, destination);
    }

    public List<TripView> getAllTrips() {
        return tripViewRepository.findAll();
    }
}
