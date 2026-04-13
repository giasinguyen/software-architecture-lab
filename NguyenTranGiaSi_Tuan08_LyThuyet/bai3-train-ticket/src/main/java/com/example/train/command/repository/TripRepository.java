package com.example.train.command.repository;

import com.example.train.command.model.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<TripEntity, Long> {
}
