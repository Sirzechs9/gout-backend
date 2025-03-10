package com.example.gout_backend.booking.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.booking.model.Booking;
import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.user.model.User;

public interface BookingRepository extends CrudRepository<Booking, Integer> {

    Optional<Booking> findOneByIdempotentKey(String idempotentKey);

    Optional<Booking> findOneByUserIdAndTourId(
            AggregateReference<User, Integer> userId,
            AggregateReference<Tour, Integer> tourId);
}
