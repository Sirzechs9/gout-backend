package com.example.gout_backend.wallet.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.user.model.User;
import com.example.gout_backend.wallet.model.UserWallet;

public interface UserWalletRepository extends CrudRepository<UserWallet, Integer>{

    Optional<UserWallet> findOneByUserId(AggregateReference<User, Integer> userId);

    
}
