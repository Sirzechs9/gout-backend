package com.example.gout_backend.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.common.enumeration.TransactionType;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.wallet.dto.UserTopupDto;
import com.example.gout_backend.wallet.dto.UserWalletInfoDto;
import com.example.gout_backend.wallet.model.Transaction;
import com.example.gout_backend.wallet.model.UserWallet;
import com.example.gout_backend.wallet.repository.TransactionRepository;
import com.example.gout_backend.wallet.repository.UserWalletRepository;

@Service
public class WalletServiceImpl implements WalletService{

    private final Logger logger =  LoggerFactory.getLogger(WalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository; 
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(TransactionRepository transactionRepository, UserWalletRepository userWalletRepository) {
        this.transactionRepository = transactionRepository;
        this.userWalletRepository = userWalletRepository;
    }
    
    @Override
    public UserWallet createConsumerWallet(int userId) {
        AggregateReference<User, Integer> UserReference = AggregateReference.to(userId);
        Instant currentTimestamp = Instant.now();
        BigDecimal initbalance = new BigDecimal("0.00");
        var wallet = new UserWallet(null, UserReference, currentTimestamp, initbalance);
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

    @Override
    @Transactional
    public UserWalletInfoDto topup(UserTopupDto body) {
        var now = Instant.now();
        var idempotentKey = body.idempotentKey();
        var userId = body.userId();
        var userWallet = getWalletByUserId(userId);
        var optionalHistoricalTransaction = transactionRepository.findOneByIdempotentKey(idempotentKey);
        if (optionalHistoricalTransaction.isPresent()){
            // if found idempotent key in transaction -> this query excuted skip to prevent duplicate excutions.
            return new UserWalletInfoDto(userWallet.userId().getId(), userWallet.balance());
        }
        // if not found idempotnet key existing do excution.
        var newTransaction = generateTopupTransaction(idempotentKey, userId, now, body.amount());
        transactionRepository.save(newTransaction);
        var updateBalance = userWallet.balance().add(body.amount());
        var updateTopupBalance = new UserWallet(userWallet.id(), userWallet.userId(), now, updateBalance);
        var updatedWallet = userWalletRepository.save(updateTopupBalance);
        return new UserWalletInfoDto(updatedWallet.userId().getId(), updatedWallet.balance());
    }

    @Override
    @Transactional
    public UserWalletInfoDto getOwnWallet(int userId) {
        
        var userWallet = getWalletByUserId(userId);
        return new UserWalletInfoDto(userWallet.userId().getId(), userWallet.balance());
    }


    private UserWallet getWalletByUserId(int userId) {
        return userWalletRepository.findOneByUserId(AggregateReference.to(userId))
                        .orElseThrow(() -> new EntityNotFoundException(String.format("wallet of userId: %d not found", userId)));
    }

    private Transaction generateTopupTransaction(String idempotentKey, Integer userId, Instant timestamp, BigDecimal amount){
        return new Transaction(null, AggregateReference.to(userId), null, timestamp, amount, TransactionType.TOPUP.name(), idempotentKey);
    }

    
    
}
