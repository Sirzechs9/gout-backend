package com.example.gout_backend.tour.service;

public interface TourCountService {

    void incrementTourCount(int tourId);

    void decrementTourCount(int tourId);
}
