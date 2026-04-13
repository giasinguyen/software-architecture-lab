package com.example.train.query.repository;

import com.example.train.query.model.TripView;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripViewRepository extends JpaRepository<TripView, Long> {
    List<TripView> findByDepartureAndDestination(String departure, String destination);
}
