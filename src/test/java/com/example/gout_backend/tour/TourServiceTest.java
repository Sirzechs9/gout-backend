package com.example.gout_backend.tour;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.example.gout_backend.common.enumeration.TourCompanyStatus;
import com.example.gout_backend.common.enumeration.TourStatus;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tour.dto.TourDto;
import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.tour.model.TourCount;
import com.example.gout_backend.tour.repository.TourCountRepository;
import com.example.gout_backend.tour.repository.TourRepository;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.repository.TourCompanyRespository;


@ExtendWith(MockitoExtension.class)
public class TourServiceTest {

    @InjectMocks
    private TourServiceImpl tourService;
    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourCompanyRespository tourCompanyRespository;

    @Mock
    private TourCountRepository tourCountRepository;


    @Test
    public void whenCreateTourThenReturnSuccess(){ 

        var activityDate = Instant.now().plus(Duration.ofDays(5));
        var payload = new TourDto(1,
                                     "Camping", 
                                     "Camping 3 days 2 nights", 
                                     "Mountain", 
                                     10, 
                                     activityDate, 
                                     TourStatus.PENDING.name());

        var mockTourCompany = new TourCompany(1, "Nine Tour", TourCompanyStatus.WAITING.name());

        when(tourCompanyRespository.findById(payload.tourCompanyId()))
            .thenReturn(Optional.of(mockTourCompany));

        var tour = new Tour(
            1,
            AggregateReference.to(mockTourCompany.id()),
            "Camping", 
            "Camping 3 days 2 nights", 
            "Mountain",
            10,
            activityDate,
            TourStatus.PENDING.name()
        );

        when(tourRepository.save(any(Tour.class)))
            .thenReturn(tour);

        var mockTourCount = new TourCount(1, AggregateReference.to(1), 0);
        when(tourCountRepository.save(any(TourCount.class)))
            .thenReturn(mockTourCount);

        var actual = tourService.createTour(payload);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(tour.id(), actual.id());
        Assertions.assertEquals(tour.tourCompanyId().getId(), actual.tourCompanyId().getId());
        Assertions.assertEquals(tour.title(), actual.title());
        Assertions.assertEquals(tour.description(), actual.description());
        Assertions.assertEquals(tour.location(), actual.location());
        Assertions.assertEquals(tour.numberOfPeople(), actual.numberOfPeople());
        Assertions.assertEquals(tour.activityDate(), actual.activityDate());
        Assertions.assertEquals(tour.status(), actual.status());

    }

    @Test
    public void whenCreateTourButCompanyNotFoundThenReturnNotFound(){
        var payload = new TourDto(1,
                                     "Camping", 
                                     "Camping 3 days 2 nights", 
                                     "Mountain", 
                                     10, 
                                     Instant.now().plus(Duration.ofDays(5)), 
                                     TourStatus.PENDING.name());

        when(tourCompanyRespository.findById(anyInt()))
            .thenThrow(new EntityNotFoundException(String.format("Tour Company Id: %s not found", 1)));
        Assertions.assertThrows(EntityNotFoundException.class, () -> tourService.createTour(payload));
    }

    @Test
    void whenGetTourByIdThenReturnSuccess(){
        var tour = new Tour(
            1,
            AggregateReference.to(1),
            "Camping", 
            "Camping 3 days 2 nights", 
            "Mountain",
            10,
            Instant.now().plus(Duration.ofDays(5)),
            TourStatus.PENDING.name()
        );

        when(tourRepository.findById(anyInt()))
            .thenReturn(Optional.of(tour));

        var actual = tourService.getTourById(1);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(tour.id(), actual.id());
        Assertions.assertEquals(tour.tourCompanyId().getId(), actual.tourCompanyId().getId());
        Assertions.assertEquals(tour.title(), actual.title());
        Assertions.assertEquals(tour.description(), actual.description());
        Assertions.assertEquals(tour.location(), actual.location());
        Assertions.assertEquals(tour.numberOfPeople(), actual.numberOfPeople());
        Assertions.assertEquals(tour.activityDate(), actual.activityDate());
        Assertions.assertEquals(tour.status(), actual.status());
    }

    @Test
    void whenGetTourByIdThenReturnNotFound(){
        when(tourRepository.findById(anyInt()))
            .thenThrow(new EntityNotFoundException(String.format("Tour Company Id: %s not found", 1)));
        Assertions.assertThrows(EntityNotFoundException.class, ()-> tourService.getTourById(1));
        
    }

    @Test
    void whenGetTourPageThenReturnSuccess(){
        List<Tour> tours = List.of();
        Page<Tour> pageTour = new PageImpl<>(tours);

        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(0, 5, sort);

        when(tourRepository.findAll(pageable))
            .thenReturn(pageTour);

        var actual = tourService.getPageTour(pageable);
        Assertions.assertTrue(actual.getContent().isEmpty()); // we set mock data to empty page
    }

}
