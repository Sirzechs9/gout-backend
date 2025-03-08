package com.example.gout_backend.tour;

  import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.common.enumeration.TourStatus;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tour.dto.TourDto;
import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.tour.model.TourCount;
import com.example.gout_backend.tour.repository.TourCountRepository;
import com.example.gout_backend.tour.repository.TourRepository;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.repository.TourCompanyRespository;


@Service
public class TourServiceImpl implements TourService{

    private final Logger logger = LoggerFactory.getLogger(TourServiceImpl.class);

    private final TourRepository tourRepository;
    private final TourCompanyRespository tourCompanyRespository;
    private final TourCountRepository tourCountRepository;

    public TourServiceImpl(TourRepository tourRepository, TourCompanyRespository tourCompanyRespository, TourCountRepository tourCountRepository) {
        this.tourRepository = tourRepository;
        this.tourCompanyRespository = tourCompanyRespository;
        this.tourCountRepository = tourCountRepository;
    }

    @Override
    @Transactional
    public Tour createTour(TourDto body) {
        var tourCompanyId = body.tourCompanyId();
        var tourCompany = tourCompanyRespository.findById(tourCompanyId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Tour Company Id: %s not found", tourCompanyId)));
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        var tour = new Tour(
            null,
            tourCompanyReference,
            body.title(), 
            body.description(), 
            body.location(), 
            body.numberOfPeople(), 
            body.activityDate(), 
            TourStatus.PENDING.name());
        var newTour = tourRepository.save(tour);
        logger.debug("Tour has been created {}",tour);
        tourCountRepository.save(new TourCount(null, AggregateReference.to(newTour.id()), 0));
        return newTour;

    }

    @Override
    public Tour getTourById(int id) {
        return tourRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("Tour id : %d not found", id)));
    }

    @Override
    public Page<Tour> getPageTour(Pageable pageable) {
        return tourRepository.findAll(pageable);
    }

}
