package com.example.gout_backend.tourcompany;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.common.enumeration.TourCompanyStatus;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tourcompany.dto.RegisterTourCompanyDto;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.model.TourCompanyLogin;
import com.example.gout_backend.tourcompany.repository.TourCompanyLoginRepository;
import com.example.gout_backend.tourcompany.repository.TourCompanyRespository;
import com.example.gout_backend.wallet.model.TourCompanyWallet;
import com.example.gout_backend.wallet.repository.TourCompanyWalletRepository;


@Service
public class TourCompanyServiceImpl implements TourCompanyService{

    private final Logger logger = LoggerFactory.getLogger(TourCompanyServiceImpl.class);

    private final TourCompanyRespository tourCompanyRespository;
    private final TourCompanyLoginRepository tourCompanyLoginRepository;
    private final TourCompanyWalletRepository tourCompanyWalletRepository;
    private final PasswordEncoder passwordEncoder;

    public TourCompanyServiceImpl(TourCompanyLoginRepository tourCompanyLoginRepository,
                                    TourCompanyRespository tourCompanyRepository,
                                    TourCompanyWalletRepository tourCompanyWalletRepository,
                                    PasswordEncoder passwordEncoder
                                ) {
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
        this.tourCompanyRespository = tourCompanyRepository;
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // For one more Repository in one method
    public TourCompany registeTourCompany(RegisterTourCompanyDto payload) {
        logger.debug("[registerTour] newly tour company is registering...");
        var companyName = payload.name();
        var tourcompany = new TourCompany(
            null,
            companyName,
            TourCompanyStatus.WAITING.name()
            );
        var newTourCompany = tourCompanyRespository.save(tourcompany);
        logger.debug("[registerTour] new tour company: {}", newTourCompany);
        createCompanyCredential(newTourCompany, payload);
        return newTourCompany;

    }



    private void createCompanyCredential(TourCompany tourCompany, RegisterTourCompanyDto payload){
        AggregateReference<TourCompany, Integer> tourConpanyReference = AggregateReference.to(tourCompany.id());
       

        var encryptedPassword = passwordEncoder.encode(payload.password());
        var companyCredential = new TourCompanyLogin(null, tourConpanyReference, payload.username(), encryptedPassword);
        var newTourCompanyLogin = tourCompanyLoginRepository.save(companyCredential);
        logger.debug("[createCredentialTour] new tour company: {}", newTourCompanyLogin);
    }



    @Override
    public TourCompany approvedTourCompany(Integer id) {
        var tourCompany = tourCompanyRespository.findById(id).
            orElseThrow(() -> new EntityNotFoundException(String.format("Tour Company Id: %s not found", id)));
        tourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        var updatedTourCompany = tourCompanyRespository.save(tourCompany);
        createCompanyWallet(id, updatedTourCompany);
        return updatedTourCompany;
    }


    private void createCompanyWallet(Integer id,TourCompany tourCompany){
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        BigDecimal initBigDecimal = new BigDecimal(.00);
        var companyWallet = new TourCompanyWallet(null, tourCompanyReference, Instant.now() , initBigDecimal);
        tourCompanyWalletRepository.save(companyWallet);
        logger.info("Created wallet for company {}", tourCompany.id());
    }
    

}
