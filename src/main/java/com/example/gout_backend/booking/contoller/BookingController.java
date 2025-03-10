package com.example.gout_backend.booking.contoller;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gout_backend.booking.dto.BookingInfoDto;
import com.example.gout_backend.booking.dto.CancelBookingDto;
import com.example.gout_backend.booking.dto.RequestBookingDto;
import com.example.gout_backend.booking.service.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto bookTour(
            @RequestHeader("idempotent-key") String idempotentKey,
            @RequestBody @Validated RequestBookingDto body,
            Authentication authentication) {
        RequestBookingDto updatedBody = new RequestBookingDto(idempotentKey, body.userId(), body.tourId());
        return bookingService.bookTour(authentication, updatedBody);
    }

    @PostMapping("/cancel")
    public BookingInfoDto cancelTour(
            @RequestHeader("idempotent-key") String idempotentKey,
            @RequestBody @Validated CancelBookingDto body,
            Authentication authentication) {
        CancelBookingDto updatedBody = new CancelBookingDto(idempotentKey, body.bookingId(), body.userId(), body.tourId());
        return bookingService.cancelTour(authentication, updatedBody);
    }

}
