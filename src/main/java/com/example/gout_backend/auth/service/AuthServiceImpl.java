package com.example.gout_backend.auth.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.auth.dto.AuthenticatedUser;
import com.example.gout_backend.auth.dto.LogOutDto;
import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.model.RefreshToken;
import com.example.gout_backend.auth.model.UserLogin;
import com.example.gout_backend.auth.repository.RefreshTokenRepository;
import static com.example.gout_backend.common.Constants.TOKEN_TYPE;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.repository.UserLoginRepository;

@Service
@Transactional
public class AuthServiceImpl implements AuthService{

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, TokenService tokenService, UserLoginRepository userLoginRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.userLoginRepository = userLoginRepository;
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
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        var now = Instant.now();
        var accessToken = tokenService.issueAccessToken(authentication, now);
        var refreshToken = tokenService.issueRefreshToken(authentication, now);

        logout(authentication);
        refreshTokenRepository.updateRefreshTokenByResource(
                                    authenticatedUser.role().name(), 
                                    authenticatedUser.userId(),
                                    true);

        // Save new refresh token
        var prepareRefreshTokenModel = new RefreshToken(
            null, 
            refreshToken, 
            now, 
            authenticatedUser.role().name(),
            authenticatedUser.userId(), 
            false
        );
        refreshTokenRepository.save(prepareRefreshTokenModel);

        return new LoginResponseDto(
            authenticatedUser.userId(), 
            TOKEN_TYPE,
            accessToken, 
            refreshToken );
    }

    @Override
    public void logout(Authentication authentication) {
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        refreshTokenRepository.updateRefreshTokenByResource(
                                    authenticatedUser.role().name(), 
                                    authenticatedUser.userId(), 
                                    true);
    }

    @Override
    public void logout(LogOutDto logOutDto) {
        refreshTokenRepository.updateRefreshTokenByResource(
            logOutDto.role(), 
            Integer.parseInt(logOutDto.sub()), 
            true);
    }
    
}
