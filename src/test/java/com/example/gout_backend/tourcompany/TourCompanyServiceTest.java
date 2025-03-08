package com.example.gout_backend.tourcompany;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gout_backend.common.enumeration.TourCompanyStatus;
import com.example.gout_backend.common.exception.EntityNotFound;
import com.example.gout_backend.tourcompany.dto.RegisterTourCompanyDto;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.model.TourCompanyLogin;
import com.example.gout_backend.tourcompany.repository.TourCompanyLoginRepository;
import com.example.gout_backend.tourcompany.repository.TourCompanyRespository;
import com.example.gout_backend.wallet.model.TourCompanyWallet;
import com.example.gout_backend.wallet.repository.TourCompanyWalletRepository;

@ExtendWith(MockitoExtension.class)
class TourCompanyServiceTest {

    @InjectMocks
    private TourCompanyServiceImpl tourCompanyService;

    @Mock
    private TourCompanyRespository tourCompanyRespository;

    @Mock
    private TourCompanyLoginRepository tourCompanyLoginRepository;

    @Mock
    private TourCompanyWalletRepository tourCompanyWalletRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void whenRegisterTourThenSuccess() {
        
        var mockTourcompany = new TourCompany(
            1,
            "Nine Tour",
            TourCompanyStatus.WAITING.name());

        when(tourCompanyRespository.save(any(TourCompany.class)))
                .thenReturn(mockTourcompany);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("EncryptedValue");

        var companyCredential = new TourCompanyLogin(1, AggregateReference.to(1), "nine", "EncryptedValue");
        
        when(tourCompanyLoginRepository.save(any(TourCompanyLogin.class)))
                .thenReturn(companyCredential);

        var payload = new RegisterTourCompanyDto(null, "Nine Tour", "nine", "123456789", null);


        var actual = tourCompanyService.registeTourCompany(payload);

        
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.id().intValue());
        Assertions.assertEquals("Nine Tour", actual.name());
        Assertions.assertEquals(TourCompanyStatus.WAITING.name(), actual.status());

    }


    @Test
    void whenApprovedTourThenSuccess() {
        var mockTourcompany = new TourCompany(1, "Nine Tour", TourCompanyStatus.WAITING.name());

        when(tourCompanyRespository.findById(anyInt())).thenReturn(Optional.of(mockTourcompany));

        var updatedTourCompany = new TourCompany(mockTourcompany.id(), mockTourcompany.name(), TourCompanyStatus.APPROVED.name());
        
        when(tourCompanyRespository.save(any(TourCompany.class)))
        .thenReturn(updatedTourCompany);


        var companyWallet = new TourCompanyWallet(null, AggregateReference.to(1), Instant.now() , new BigDecimal(.00));

        when(tourCompanyWalletRepository.save(any(TourCompanyWallet.class))).thenReturn(companyWallet);

        var actual = tourCompanyService.approvedTourCompany(1);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.id().intValue());
        Assertions.assertEquals("Nine Tour", actual.name());
        Assertions.assertEquals(TourCompanyStatus.APPROVED.name(), actual.status());

    }


    @Test
    void whenApprovedTourButTourCompanyNotFoundThenError() {
        when(tourCompanyRespository.findById(anyInt()))
            .thenThrow(new EntityNotFound(String.format("Tour Company Id: %s not found", 1)));
        Assertions.assertThrows(EntityNotFound.class, ()-> tourCompanyService.approvedTourCompany(1));
    }

}
