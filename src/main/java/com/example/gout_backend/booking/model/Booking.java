package com.example.gout_backend.booking.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.user.model.User;

@Table("booking")
public record Booking(
    @Id Integer id,
        AggregateReference<User, Integer> userId,
        AggregateReference<Tour, Integer> tourId,
        String state,
        Instant bookingDate,
        Instant lastUpdated,
        String idempotentKey
) {

}
