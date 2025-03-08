package com.example.gout_backend.tourcompany;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.gout_backend.common.enumeration.TourCompanyStatus;
import com.example.gout_backend.common.exception.EntityNotFound;
import com.example.gout_backend.tourcompany.dto.RegisterTourCompanyDto;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(TourCompanyController.class)
class TourCompanyControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean //Bean -> spring use for manage inversion of control / dependencise injection.
    private TourCompanyService tourCompanyService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenCreateTourCompanySuccessful() throws Exception{

        var mockTourCompany = new TourCompany(
                 1, 
                 "Nine Tour", 
                 TourCompanyStatus.WAITING.name());
        
        when(tourCompanyService.registeTourCompany(any(RegisterTourCompanyDto.class)))
                .thenReturn(mockTourCompany);

        var payload = new RegisterTourCompanyDto(
                null, "Nine Tour",
                "nine", 
                "123456789",
                null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/tour-companies")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(payload))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void whenApprovedTourCompanyThenSuccess() throws Exception{

        var mockTourCompany = new TourCompany(
                 1, 
                 "Nine Tour", 
                 TourCompanyStatus.APPROVED.name());
        when(tourCompanyService.approvedTourCompany(anyInt()))
            .thenReturn(mockTourCompany);
        mockMvc.perform(
            MockMvcRequestBuilders.post(String.format("/api/v1/tour-companies/%d/approved", 1))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(TourCompanyStatus.APPROVED.name()));
        
    }

    @Test
    void whenApprovedTourCompanyButNotFound() throws Exception{
        when(tourCompanyService.approvedTourCompany(anyInt()))
            .thenThrow(new EntityNotFound());
        mockMvc.perform(

            MockMvcRequestBuilders.post(String.format("/api/v1/tour-companies/%d/approved", 1)))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
        
    }


}
