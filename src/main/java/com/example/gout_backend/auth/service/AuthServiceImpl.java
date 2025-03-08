package com.example.gout_backend.auth.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.model.UserLogin;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.repository.UserLoginRepository;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserLoginRepository userLoginRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userLoginRepository = userLoginRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Optional<UserLogin> findCredentialByUsername(String email) {
        return userLoginRepository.findOneByEmail(email);
        
    }

    @Override
    public UserLogin createConsumerCredential(int userId, String email, String password) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);    
        var encryptedPassword = passwordEncoder.encode(password);
        var userCredential = new UserLogin(null, userReference, email, encryptedPassword);
        var createdCredential = userLoginRepository.save(userCredential);
        logger.info("created credential for user {}", userId);
        return createdCredential;
    }

    @Override
    public Optional<UserLogin> findCredentialByUserId(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        return userLoginRepository.findOneByUserId(userReference);
    }

    @Override
    public void deleteCredentialByUserId(int userId) {
        var credential = findCredentialByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Credential for users %d not found", userId)));
        userLoginRepository.delete(credential);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto body) {
        var authInfo = new UsernamePasswordAuthenticationToken(body.username(), body.password());
        var authentication = authenticationManager.authenticate(authInfo);
        return new LoginResponseDto(null, "Simple token");
    }
    
}
