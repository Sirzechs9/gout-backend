package com.example.gout_backend.wallet;

import com.example.gout_backend.wallet.dto.UserTopupDto;
import com.example.gout_backend.wallet.dto.UserWalletInfoDto;
import com.example.gout_backend.wallet.model.UserWallet;

public interface WalletService {

    UserWallet createConsumerWallet(int userId);

    void deleteWalletByUserId(int userId);



    UserWalletInfoDto getOwnWallet(int userId);

    UserWalletInfoDto topup(UserTopupDto body);
}
