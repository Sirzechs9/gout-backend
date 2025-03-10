package com.example.gout_backend.tour;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.gout_backend.common.enumeration.TourStatus;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tour.dto.TourDto;
import com.example.gout_backend.tour.model.Tour;
import com.example.gout_backend.tour.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.InternalServerErrorException;

@WebMvcTest(TourController.class)
public class TourControllerTest {

        
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean //Bean -> spring use for manage inversion of control / dependencise injection.
    private TourService tourService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void whenGetTourByIdThenSuccessful() throws Exception{

            var mockTour = new Tour(
            1,
            AggregateReference.to(1),
            "Camping", 
            "Camping 3 days 2 nights", 
            "Mountain",
            10,
            Instant.now().plus(Duration.ofDays(5)),
            TourStatus.PENDING.name()
            );

        when(tourService.getTourById(anyInt()))
            .thenReturn(mockTour);

        mockMvc.perform(
            MockMvcRequestBuilders.get(String.format("/api/v1/tours/%d", 1)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

    }

    @Test
    void whenGetTourByIdButNotFoundThenReturn404() throws Exception{

        when(tourService.getTourById(anyInt()))
            .thenThrow(new EntityNotFoundException());
        
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/v1/tours/%d", 1)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        
    }


    @Test
    void whenGetTourByIdButServerErrorThenReturn404() throws Exception{

        when(tourService.getTourById(anyInt()))
            .thenThrow(new InternalServerErrorException("mock error"));
        
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/v1/tours/%d", 1)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        
    }



    @Test
    void whenCreateTourThenReturnSuccess() throws Exception{

        var payload = new TourDto(1,
                                     "Camping", 
                                     "Camping 3 days 2 nights", 
                                     "Mountain", 
                                     10, 
                                     Instant.now().plus(Duration.ofDays(5)), 
                                     TourStatus.PENDING.name());

        var mockTour = new Tour(
                1,
                AggregateReference.to(1),
                "Camping", 
                "Camping 3 days 2 nights", 
                "Mountain",
                10,
                Instant.now().plus(Duration.ofDays(5)),
                TourStatus.PENDING.name()
        );

        when(tourService.createTour(any(TourDto.class)))
            .thenReturn(mockTour);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/tours")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(payload))
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void whenCreateTourButMissingSomeFieldsThenReturn400() throws Exception{

        var payload = new TourDto(1,
                                     null,
                                     null,
                                     "Mountain", 
                                     10, 
                                     Instant.now().plus(Duration.ofDays(5)), 
                                     TourStatus.PENDING.name());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/tours")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(payload))
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    
    @Test
    void whenGetPageTourThenReturnSuccessful() throws Exception{

        var mockTour = new Tour(
            1,
            AggregateReference.to(1),
            "Camping", 
            "Camping 3 days 2 nights", 
            "Mountain",
            10,
            Instant.now().plus(Duration.ofDays(5)),
            TourStatus.PENDING.name()
    );  
        List<Tour> tours = List.of(mockTour);
        Page<Tour> pageTour = new PageImpl<>(tours);
        when(tourService.getPageTour(any(Pageable.class)))
            .thenReturn(pageTour);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/tours?page=0&size=2&sortField=id&sortDirection=asc")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }


    @Test
    void getPageTourButForgotRequiredQueryString() throws Exception{

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/tours?page?=0&size=2&sortField=id&sortDirection=asc")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
    )
    .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
}
