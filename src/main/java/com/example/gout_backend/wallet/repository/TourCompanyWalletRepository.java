package com.example.gout_backend.wallet.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.wallet.model.TourCompanyWallet;

public interface TourCompanyWalletRepository extends CrudRepository<TourCompanyWallet, Integer> {

    Optional<TourCompanyWallet> findOneByTourCompanyId(AggregateReference<TourCompany, Integer> tourCompanyId);
}