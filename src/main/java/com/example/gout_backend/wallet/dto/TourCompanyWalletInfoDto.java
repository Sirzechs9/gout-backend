package com.example.gout_backend.wallet.dto;

import java.math.BigDecimal;

public record TourCompanyWalletInfoDto(
    Integer tourCompanyId,
    BigDecimal balance
) {

}
