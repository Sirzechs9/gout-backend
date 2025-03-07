package com.example.gout_backend.tour.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record TourDto(
    Integer tourCompanyId, 
    @NotBlank String title,
    @NotBlank String description,
    @NotBlank String location,
    int numberOfPeople,
    @NotBlank Instant activityDate,
    String status    

) {

}
