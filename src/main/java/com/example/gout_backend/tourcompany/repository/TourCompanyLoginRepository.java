package com.example.gout_backend.tourcompany.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.model.TourCompanyLogin;

public interface TourCompanyLoginRepository extends CrudRepository<TourCompanyLogin, Integer>{

    Optional<TourCompanyLogin> findOneByUsername(String username);

    Optional<TourCompanyLogin> findOneByTourCompanyId(AggregateReference<TourCompany, Integer> tourCompanyId);
}
