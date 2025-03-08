package com.example.gout_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.auth.model.UserLogin;
import com.example.gout_backend.user.model.User;

public interface UserLoginRepository extends CrudRepository<UserLogin, Integer>{

    Optional<UserLogin> findOneByEmail(String email);

    Optional<UserLogin> findOneByUserId(AggregateReference<User, Integer> userId);
}
