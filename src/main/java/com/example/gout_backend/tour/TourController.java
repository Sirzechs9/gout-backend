package com.example.gout_backend.tour;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gout_backend.tour.dto.TourDto;
import com.example.gout_backend.tour.model.Tour;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    
    private final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    // CRUD - Tour
    // C - Create Tour
    // R - Read, Get 1 tour
    // U - Update on specific record
    // D - Delete from InMemory DB

    @GetMapping
    //Paination in spring Boot (Spring Data JDBC)
    public Page<Tour> getTours(
        @RequestParam(required = true) int page, // page number
        @RequestParam(required = true) int size,
        @RequestParam(required = true) String sortField,
        @RequestParam(required = true) String sortDirection
    ) {
        //1-100 tours
        //Size:20 Page:0 -> [1,20]  Page:1 ->[21,40]  Page:2 ->[41,60]..
        //Page - 2
        //Sort ASC, DESC 
        
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection.toUpperCase()), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return tourService.getPageTour(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable int id) {
        logger.info("Get tourId: {}", id);
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody @Validated TourDto body) {
        var newTour = tourService.createTour(body);
        var location = String.format("http://localhost/api/v1/tours/%d", newTour.id());
        return ResponseEntity.created(URI.create(location)).body(newTour);
    }


    // @PutMapping("/{id}")
    // public Tour putMethodName(@PathVariable int id, @RequestBody Tour tour) {
    //     var updatedTour = new Tour(
    //             id,
    //             tour.title(),
    //             tour.maxPeople());
    //     tourInMemDb.put(id, updatedTour);
    //     logger.info("Updated tour: {}", tourInMemDb.get(id));
    //     return tourInMemDb.get(id);
    // }

    // @DeleteMapping("/{id}")
    // public String deleteTour(@PathVariable int id) {
    //     if (!tourInMemDb.containsKey(id)) {
    //         logger.error("tourId: {} not found", id);
    //         return "Failed";
    //     }
    //     tourInMemDb.remove(id);
    //     logger.info("success delete tourId: {}", id);
    //     return "Success to delete " + id;
    // }
}