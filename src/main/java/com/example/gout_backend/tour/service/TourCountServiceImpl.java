package com.example.gout_backend.tour.service;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tour.model.TourCount;
import com.example.gout_backend.tour.repository.TourCountRepository;

@Service
public class TourCountServiceImpl implements TourCountService {
    private final TourCountRepository tourCountRepository;

    public TourCountServiceImpl(TourCountRepository tourCountRepository) {
        this.tourCountRepository = tourCountRepository;
    }

    @Override
    public void incrementTourCount(int tourId) {
        var tourCount = tourCountRepository.findOneByTourId(AggregateReference.to(tourId))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("TourCount for tourId: %d not found", tourId)));
        var newAmount = tourCount.amount() + 1;
        var prepareTourCout = new TourCount(tourCount.id(), tourCount.tourId(), newAmount);
        tourCountRepository.save(prepareTourCout);
    }

    @Override
    public void decrementTourCount(int tourId) {
        var tourCount = tourCountRepository.findOneByTourId(AggregateReference.to(tourId))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("TourCount for tourId: %d not found", tourId)));
        var newAmount = tourCount.amount() - 1;
        var prepareTourCout = new TourCount(tourCount.id(), tourCount.tourId(), newAmount);
        tourCountRepository.save(prepareTourCout);
    }
}