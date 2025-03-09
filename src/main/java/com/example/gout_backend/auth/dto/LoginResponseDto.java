package com.example.gout_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginResponseDto(
   Integer userId,
   String tokenType,
   String accessToken,
   String refreshToken
) {

}
