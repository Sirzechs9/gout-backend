package com.example.gout_backend.tour;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.common.enumeration.TourStatus;
import com.example.gout_backend.common.exception.EntityNotFound;
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
            .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", tourCompanyId)));
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompanyId.id());
        var tour = new Tour(
            null,
            null,
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTourById'");
    }

    @Override
    public Page<Tour> getPageTour() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPageTour'");
    }

}
