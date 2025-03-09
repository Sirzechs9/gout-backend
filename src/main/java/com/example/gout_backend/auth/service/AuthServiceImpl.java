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
import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.dto.LogoutDto;
import com.example.gout_backend.auth.dto.RefreshTokenDto;
import com.example.gout_backend.auth.model.RefreshToken;
import com.example.gout_backend.auth.model.UserLogin;
import com.example.gout_backend.auth.repository.RefreshTokenRepository;
import com.example.gout_backend.auth.repository.UserLoginRepository;
import static com.example.gout_backend.common.Constants.TOKEN_TYPE;
import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.common.exception.RefreshTokenExpiredException;
import com.example.gout_backend.tourcompany.model.TourCompany;
import com.example.gout_backend.tourcompany.repository.TourCompanyLoginRepository;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.repository.UserRepositoy;


@Service
public class AuthServiceImpl implements AuthService{

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepositoy userRepositoy;
    private final TourCompanyLoginRepository tourCompanyLoginRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, TokenService tokenService, UserLoginRepository userLoginRepository, UserRepositoy userRepositoy, TourCompanyLoginRepository tourCompanyLoginRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.userLoginRepository = userLoginRepository;
        this.userRepositoy = userRepositoy;
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
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
    @Transactional
    public LoginResponseDto login(LoginRequestDto body) {
        var authInfo = new UsernamePasswordAuthenticationToken(body.username(), body.password());
        var authentication = authenticationManager.authenticate(authInfo);
        var authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        var now = Instant.now();
        var accessToken = tokenService.issueAccessToken(authentication, now);
        var refreshToken = tokenService.issueRefreshToken();

        logout(authentication);

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
    public void logout(LogoutDto logoutDto) {
        String usage = determineUsageFromRoles(logoutDto.roles());
        refreshTokenRepository.updateRefreshTokenByResource(
            logoutDto.roles(), 
            Integer.parseInt(logoutDto.sub()), 
            true);
    }

    private String determineUsageFromRoles(String roles) {
       
        if (roles == null) {
            return "default_usage"; 
        }
        return roles; 
    }
    

    @Override
    @Transactional
    public LoginResponseDto issueNewRefreshToken(RefreshTokenDto body) {

        // Check refresh token is exist?
        var refreshTokenEntity = refreshTokenRepository.findOneByToken(body.refreshToken())
                                .orElseThrow(() -> new EntityNotFoundException("this refresh token not found"));
        var resourceId = refreshTokenEntity.resourceId();
        // Expired? - DB -> UssuedDate
       if(tokenService.isRefreshTokenExpired(refreshTokenEntity)) {
            logout(new LogoutDto(String.valueOf(resourceId), refreshTokenEntity.usage()));
            throw new RefreshTokenExpiredException("this refresh token is expired");
       }
        //Token almost expired => refresh token rotation

      
        String newAccessToken = switch(RoleEnum.valueOf(body.usage())) {
            case RoleEnum.COMPANY -> {
                    AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(resourceId);
                    var credential = tourCompanyLoginRepository.findOneByTourCompanyId(tourCompanyReference) 
                                    .orElseThrow(()-> new EntityNotFoundException(String.format("User id %d not found", resourceId))); 
                    
                    yield tokenService.issueAccessToken(credential, Instant.now());
            }
            default -> {  
                var user = userRepositoy.findById(resourceId)
                                    .orElseThrow(()-> new EntityNotFoundException(String.format("User id %d not found", resourceId)));
                var credential = findCredentialByUserId(user.id())
                                    .orElseThrow(() -> new EntityNotFoundException(String.format("Credential for user Id : %d not found", user.id())));
                yield tokenService.issueAccessToken(credential, Instant.now());
            }
        };

         
        
        var refreshToken = tokenService.rotateRefreshTokenIfNeed(refreshTokenEntity);
        //Check if refresh token change -> change old refresh token to expired
        if(!refreshToken.equals(refreshTokenEntity.token())) {
            var updatedRefreshTokenEnity = new RefreshToken(
                refreshTokenEntity.Id(), 
                refreshTokenEntity.token(), 
                refreshTokenEntity.issuedDate(), 
                refreshTokenEntity.usage(),
                refreshTokenEntity.resourceId(), 
                true);
            refreshTokenRepository.save(updatedRefreshTokenEnity);
            var preparedRefreshTokenModel = new RefreshToken(
                null,
                refreshToken, 
                Instant.now(),
                refreshTokenEntity.usage(), 
                refreshTokenEntity.resourceId(), 
                false);
                refreshTokenRepository.save(preparedRefreshTokenModel);
        }
        return new LoginResponseDto(
            refreshTokenEntity.resourceId(), 
            TOKEN_TYPE,
            newAccessToken,
            refreshToken);
    }
    
}
