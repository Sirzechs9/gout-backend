package com.example.gout_backend.tour.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.gout_backend.tour.model.Tour;

public interface TourRepository extends PagingAndSortingRepository<Tour, Integer> {

}
