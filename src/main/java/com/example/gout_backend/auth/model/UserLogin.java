package com.example.gout_backend.auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import com.example.gout_backend.user.model.User;

@Table("user_login")
public record UserLogin(
    @Id Integer id,
    AggregateReference<User, Integer> userId,
    String email,
    String password ) {
}
