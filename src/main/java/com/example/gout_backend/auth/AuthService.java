package com.example.gout_backend.auth;

import java.util.Optional;

public interface  AuthService {

    Optional<UserLogin>findCredentialByUsername(String email);

    UserLogin createConsumerCredential(int userId, String email, String password);

    Optional<UserLogin> findCredentialByUserId(int userId);

    void deleteCredentialByUserId(int userId);
}
