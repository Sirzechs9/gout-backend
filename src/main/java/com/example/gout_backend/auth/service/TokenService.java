package com.example.gout_backend.auth.service;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.gout_backend.auth.dto.AuthenticatedUser;
import com.example.gout_backend.auth.model.RefreshToken;
import com.example.gout_backend.auth.model.UserLogin;
import com.example.gout_backend.tourcompany.model.TourCompanyLogin;

@Service
public class TokenService {

    

    private static final String ISSUER = "gout-backend";
    private static final String ROLES_CLAIM = "roles";
    private static final int TIME_FOR_ROTATE_SECONDS = 120;


    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpiredInSeconds;
    private final long refreshTokenExpiredInSeconds;
    private final CustomUserDetailService customUserDetailService;


    public TokenService(
        JwtEncoder jwtEncoder, 
            @Value("${token.access-token-expired-in-seconds}") long accessTokenExpiredInSeconds,
            @Value("${token.refresh-token-expired-in-seconds}") long refreshTokenExpiredInSeconds,
            CustomUserDetailService customUserDetailService) {
        this.accessTokenExpiredInSeconds = accessTokenExpiredInSeconds;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenExpiredInSeconds = refreshTokenExpiredInSeconds;
        this.customUserDetailService = customUserDetailService;
    }


    public String issueAccessToken(Authentication auth, Instant issueDate) {
        return generateToken(auth, issueDate, accessTokenExpiredInSeconds);
    }

    public String issueAccessToken(UserLogin userLogin, Instant issueDate){
        AuthenticatedUser  userDetails = (AuthenticatedUser) customUserDetailService.loadUserByUsername(userLogin.email());
        return generateToken(userDetails, issueDate, accessTokenExpiredInSeconds);

    }

    public String issueAccessToken(TourCompanyLogin tourCompanyLogin, Instant issueDate){
        AuthenticatedUser  userDetails = (AuthenticatedUser) customUserDetailService.loadUserByUsername(tourCompanyLogin.username());
        return generateToken(userDetails, issueDate, accessTokenExpiredInSeconds);
    }

    public String issueRefreshToken() {
        return UUID.randomUUID().toString();                                              
    }

    public String generateToken(AuthenticatedUser auth, Instant issueDate, long expiredInSeconds) {
        return generateToken(auth.userId(), auth.getAuthorities(), issueDate, expiredInSeconds);
    }

    // งงจัดเลอ
    public String generateToken(Authentication auth, Instant issueDate, long expiredInSeconds) {
        var authenticatedUser = (AuthenticatedUser) auth.getPrincipal();  
        return generateToken(authenticatedUser.userId(), auth.getAuthorities(), issueDate, expiredInSeconds);
    }

    private String generateToken(Integer userId, Collection<? extends GrantedAuthority> authorities, Instant issueDate, long expiredInString) {
         
        Instant expire = issueDate.plusSeconds(expiredInString);
        String scope = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            .issuedAt(issueDate)
            .subject(String.valueOf(userId))
            .claim(ROLES_CLAIM, scope)
            .expiresAt(expire)
            .build();

        return endcodeClaimToJwt(claims);
    }

    public String endcodeClaimToJwt(JwtClaimsSet claims){
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isRefreshTokenExpired(RefreshToken refreshToken) {
        var issuedDate = refreshToken.issuedDate();
        var expiredDate = issuedDate.plusSeconds(refreshTokenExpiredInSeconds);
        var now = Instant.now();
        return now.isAfter(expiredDate);
    }

    public String rotateRefreshTokenIfNeed(RefreshToken refreshTokenEntity){
        //TIME_FOR_ROTATE_MINS
        var issuedDate = refreshTokenEntity.issuedDate();
        var expiredDate = issuedDate.plusSeconds(refreshTokenExpiredInSeconds);
        var thresholdToRotateDate = expiredDate.minusSeconds(TIME_FOR_ROTATE_SECONDS);
        var now = Instant.now();
        if (now.isAfter(thresholdToRotateDate) ){
            return issueRefreshToken();
        }
        return refreshTokenEntity.token();
        
    }
}
