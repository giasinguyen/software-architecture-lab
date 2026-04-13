package com.example.train.config;

import com.example.train.command.model.TripEntity;
import com.example.train.command.repository.TripRepository;
import com.example.train.query.model.TripView;
import com.example.train.query.repository.TripViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TripRepository tripRepository;
    private final TripViewRepository tripViewRepository;

    @Override
    public void run(String... args) {
        if (tripRepository.count() == 0) {
            createTrip("SE1", "Ha Noi", "Sai Gon", LocalDateTime.now().plusDays(1), 50);
            createTrip("SE2", "Sai Gon", "Ha Noi", LocalDateTime.now().plusDays(2), 50);
            createTrip("SE3", "Ha Noi", "Da Nang", LocalDateTime.now().plusDays(1), 40);
        }
    }

    private void createTrip(String trainNumber, String departure, String destination,
                            LocalDateTime departureTime, int totalSeats) {
        TripEntity trip = tripRepository.save(TripEntity.builder()
                .trainNumber(trainNumber).departure(departure).destination(destination)
                .departureTime(departureTime).totalSeats(totalSeats).availableSeats(totalSeats)
                .build());
        tripViewRepository.save(new TripView(trip.getId(), trip.getTrainNumber(),
                trip.getDeparture(), trip.getDestination(), trip.getDepartureTime(),
                trip.getTotalSeats(), trip.getAvailableSeats()));
    }
}
