package com.example.gout_backend.tour;

import org.springframework.data.domain.Page;

import com.example.gout_backend.tour.dto.TourDto;
import com.example.gout_backend.tour.model.Tour;

public interface TourService {


    Tour createTour(TourDto body);

    Tour getTourById(int id);

    Page<Tour> getPageTour();
}
