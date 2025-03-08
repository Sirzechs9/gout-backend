package com.example.gout_backend.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.wallet.model.UserWallet;
import com.example.gout_backend.wallet.repository.UserWalletRepository;

@Service
public class WalletServiceImpl implements WalletService{

    private final Logger logger =  LoggerFactory.getLogger(WalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository; 

    public WalletServiceImpl(UserWalletRepository userWalletRepository) {
        this.userWalletRepository = userWalletRepository;
    }
    
    @Override
    public UserWallet createConsumerWallet(int userId) {
        AggregateReference<User, Integer> UserReference = AggregateReference.to(userId);
        Instant currentTimestamp = Instant.now();
        BigDecimal balance = new BigDecimal("0.00");
        var wallet = new UserWallet(null, UserReference, currentTimestamp, balance);
        userWalletRepository.save(wallet);
        logger.info("created wallet for user: {}",userId);
        return wallet;
    }

    @Override
    public void deleteWalletByUserId(int userId) {
           var wallet = userWalletRepository.findOneByUserId(AggregateReference.to(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("wallet for users %d not found", userId)));
        userWalletRepository.delete(wallet);
    }

        

    
    
}
