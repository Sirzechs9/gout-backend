package com.example.gout_backend.booking.dto;

import jakarta.validation.constraints.NotNull;

public record RequestBookingDto(
    String idempotentKey,
        @NotNull Integer userId,
        @NotNull Integer tourId
) {

}
