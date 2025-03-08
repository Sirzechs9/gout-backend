package com.example.gout_backend.auth;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Table("user_login")
public record UserLogin(
    @Id Integer id,
    AggregateReference<User, Integer> userId,
    String email,
    String password ) {
}
