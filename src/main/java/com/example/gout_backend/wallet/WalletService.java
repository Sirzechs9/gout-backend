package com.example.gout_backend.wallet;

import java.math.BigDecimal;

import org.springframework.data.util.Pair;

import com.example.gout_backend.booking.model.Booking;
import com.example.gout_backend.common.enumeration.TransactionType;
import com.example.gout_backend.wallet.dto.UserTopupDto;
import com.example.gout_backend.wallet.dto.UserWalletInfoDto;
import com.example.gout_backend.wallet.model.TourCompanyWallet;
import com.example.gout_backend.wallet.model.UserWallet;

public interface WalletService {

    UserWallet createConsumerWallet(int userId);

    void deleteWalletByUserId(int userId);



    UserWalletInfoDto getOwnWallet(int userId);

    UserWalletInfoDto topup(UserTopupDto body);

    Pair<UserWallet, TourCompanyWallet> getUserWalletAndTourCompanyWallet(Booking bookingData);

    Pair<UserWallet, TourCompanyWallet> transfer(
            UserWallet userWallet,
            TourCompanyWallet tourCompanyWallet,
            BigDecimal amount,
            TransactionType type);
}
