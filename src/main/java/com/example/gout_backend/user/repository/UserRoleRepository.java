package com.example.gout_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.model.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer>{
    Optional<UserRole> findOneByUserId(AggregateReference<User, Integer> userId);
}
