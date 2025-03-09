package com.example.gout_backend.auth.dto;

public record LogoutDto(
    String sub,
    String roles
) {

}
