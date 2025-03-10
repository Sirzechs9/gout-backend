package com.example.gout_backend.booking.service;

import org.springframework.security.core.Authentication;

import com.example.gout_backend.booking.dto.BookingInfoDto;
import com.example.gout_backend.booking.dto.CancelBookingDto;
import com.example.gout_backend.booking.dto.RequestBookingDto;

public interface BookingService {

    BookingInfoDto bookTour(Authentication authentication, RequestBookingDto body);

    BookingInfoDto cancelTour(Authentication authentication, CancelBookingDto body);
}
