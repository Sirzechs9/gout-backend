package com.example.gout_backend.wallet.dto;

import java.math.BigDecimal;

public record UserWalletInfoDto(
    Integer resourceId,
    BigDecimal balance
) {

}
