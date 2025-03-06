package com.example.gout_backend.tourcompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gout_backend.tourcompany.dto.RegisterTourCompanyDto;
import com.example.gout_backend.tourcompany.model.TourCompany;



@RestController
@RequestMapping("/api/v1/tour-companies")
public class TourCompanyController {

    private final Logger logger = LoggerFactory.getLogger(TourCompanyController.class);

    private final TourCompanyService tourCompanyService;

    public TourCompanyController(TourCompanyService tourCompanyService) {
        this.tourCompanyService = tourCompanyService;
    }

    @PostMapping
    public ResponseEntity<TourCompany> registeNewTourCompany(@RequestBody @Validated RegisterTourCompanyDto body) {
        //ResponceEntity => can control http status
        var tourcompany = tourCompanyService.registeTourCompany(body);
        return ResponseEntity.ok(tourcompany);
    }

    @PostMapping("/{id}/approved")
    public ResponseEntity<TourCompany> approvedNewTourCompany(@PathVariable Integer id) {
        var approvedCompany = tourCompanyService.approvedTourCompany(id);
        logger.debug("[approvedCompany] company id: {} is approved", approvedCompany.id());
        return ResponseEntity.ok(approvedCompany);
    }
    
        
    


}
