package com.example.gout_backend.tour.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.tour.model.TourCount;

public interface TourCountRepository extends CrudRepository<TourCount, Integer> {

    @Lock(LockMode.PESSIMISTIC_WRITE)
    Optional<TourCount> findOneByTourId(AggregateReference<Tour, Integer> tourId);
}
