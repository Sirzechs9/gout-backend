package com.example.gout_backend.tour.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.tour.model.TourCount;

public interface TourCountRepository extends CrudRepository<TourCount, Integer> {

}
