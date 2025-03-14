package com.example.gout_backend.tour.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import com.example.gout_backend.tourcompany.model.TourCompany;

@Table("tour")
public record Tour(
    @Id Integer id,
    AggregateReference<TourCompany, Integer> tourCompanyId,
    String title,
    String description,
    String location,
    int numberOfPeople,
    Instant activityDate,
    String status) {

}
