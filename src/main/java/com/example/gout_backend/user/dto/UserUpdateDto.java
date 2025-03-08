package com.example.gout_backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(
    @NotBlank String firstName,
    @NotBlank String lastName
) {

}
