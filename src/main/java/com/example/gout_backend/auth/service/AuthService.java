package com.example.gout_backend.auth.service;

import java.util.Optional;

import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.model.UserLogin;

public interface  AuthService {

    Optional<UserLogin>findCredentialByUsername(String email);

    UserLogin createConsumerCredential(int userId, String email, String password);

    Optional<UserLogin> findCredentialByUserId(int userId);

    void deleteCredentialByUserId(int userId);

    LoginResponseDto login(LoginRequestDto body);
}
