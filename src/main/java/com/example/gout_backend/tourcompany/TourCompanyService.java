package com.example.gout_backend.tourcompany;

import com.example.gout_backend.tourcompany.dto.RegisterTourCompanyDto;
import com.example.gout_backend.tourcompany.model.TourCompany;

public interface TourCompanyService {

    TourCompany registeTourCompany(RegisterTourCompanyDto payload);

    TourCompany approvedTourCompany(Integer id);
}
