package com.example.gout_backend.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.booking.model.Booking;
import com.example.gout_backend.common.enumeration.TransactionType;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.payment.TransactionRepository;
import com.example.gout_backend.payment.TransactionUtil;
import com.example.gout_backend.tour.repository.TourRepository;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.wallet.dto.UserTopupDto;
import com.example.gout_backend.wallet.dto.UserWalletInfoDto;
import com.example.gout_backend.wallet.model.TourCompanyWallet;
import com.example.gout_backend.wallet.model.UserWallet;
import com.example.gout_backend.wallet.repository.TourCompanyWalletRepository;
import com.example.gout_backend.wallet.repository.UserWalletRepository;

@Service
public class WalletServiceImpl implements WalletService{

    private final Logger logger =  LoggerFactory.getLogger(WalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository; 
    private final TransactionRepository transactionRepository;
    private final TourCompanyWalletRepository tourCompanyWalletRepository;
    private final TourRepository tourRepository;

     public WalletServiceImpl(
            TourCompanyWalletRepository tourCompanyWalletRepository,
            TourRepository tourRepository,
            TransactionRepository transactionRepository,
            UserWalletRepository userWalletRepository) {
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
        this.tourRepository = tourRepository;
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
        var newTransaction = TransactionUtil.generateTopupTransaction(idempotentKey, userId, now, body.amount());
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

    @Override
    public Pair<UserWallet, TourCompanyWallet> getUserWalletAndTourCompanyWallet(Booking bookingData) {
        var userId = bookingData.userId();
        var tourId = bookingData.tourId();
        var userWallet = userWalletRepository.findOneByUserId(userId)
                .orElseThrow(EntityNotFoundException::new);
        var tourInfo = tourRepository.findById(tourId.getId())
                .orElseThrow(EntityNotFoundException::new);
        var tourCompanyWallet = tourCompanyWalletRepository
                .findOneByTourCompanyId(tourInfo.tourCompanyId())
                .orElseThrow(EntityNotFoundException::new);
        return Pair.of(userWallet, tourCompanyWallet);
    }

   @Override
    public Pair<UserWallet, TourCompanyWallet> transfer(
            UserWallet userWallet,
            TourCompanyWallet tourCompanyWallet,
            BigDecimal amount,
            TransactionType type) {
        return switch (type) {
            case TransactionType.BOOKING -> {
                var prepareUserWallet = new UserWallet(
                        userWallet.id(),
                        userWallet.userId(),
                        Instant.now(),
                        userWallet.balance().subtract(amount)
                );
                var prepaTourCompanyWallet = new TourCompanyWallet(
                        tourCompanyWallet.id(),
                        tourCompanyWallet.tourCompanyId(),
                        Instant.now(),
                        tourCompanyWallet.balance().add(amount)
                );
                // Don't forget to apply pessimistic lock here
                var updateUserWallet = userWalletRepository.save(prepareUserWallet);
                var updateTourCompanyWallet = tourCompanyWalletRepository.save(prepaTourCompanyWallet);
                yield Pair.of(updateUserWallet, updateTourCompanyWallet);
            }
            case TransactionType.REFUND -> {
                var prepareUserWallet = new UserWallet(
                        userWallet.id(),
                        userWallet.userId(),
                        Instant.now(),
                        userWallet.balance().add(amount)
                );
                var prepaTourCompanyWallet = new TourCompanyWallet(
                        tourCompanyWallet.id(),
                        tourCompanyWallet.tourCompanyId(),
                        Instant.now(),
                        tourCompanyWallet.balance().subtract(amount)
                );
                // Don't forget to apply pessimistic lock here
                var updateUserWallet = userWalletRepository.save(prepareUserWallet);
                var updateTourCompanyWallet = tourCompanyWalletRepository.save(prepaTourCompanyWallet);
                yield Pair.of(updateUserWallet, updateTourCompanyWallet);
            }
            default -> {
                throw new IllegalArgumentException("Invalid Transaction Type");
            }
        };

    
    }


}