package com.example.gout_backend.wallet.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.wallet.model.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer>{

    Optional<Transaction> findOneByIdempotentKey(String idempotentKdy);
}
