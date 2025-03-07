package com.example.gout_backend.tour;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // Get All
    // @GetMapping
    // public List<Tour> getTours() {
    //     logger.info("Get all tours");
    //     return tourInMemDb.entrySet().stream()
    //             .map(e -> e.getValue())
    //             .toList();
    // }

    // Get single
    // @GetMapping("/{id}")
    // public Tour getTourById(@PathVariable int id) {
    //     logger.info("Get tourId: {}", id);
    //     return Optional.ofNullable(tourInMemDb.get(id))
    //             .orElseThrow(() -> {
    //                 logger.error("tourId: {} not found", id);
    //                 return new RuntimeException("Not found");
    //             });
    // }

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