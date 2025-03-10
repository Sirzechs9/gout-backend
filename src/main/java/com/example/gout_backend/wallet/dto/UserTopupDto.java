package com.example.gout_backend.wallet.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UserTopupDto(
    @DecimalMin(value = "0.0", inclusive = false)
    @NotNull BigDecimal amount,
    Integer userId,
    String idempotentKey
) {

}
