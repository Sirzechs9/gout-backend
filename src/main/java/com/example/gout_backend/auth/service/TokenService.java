package com.example.gout_backend.auth.service;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.gout_backend.auth.dto.AuthenticatedUser;

@Service
public class TokenService {

    

    private static final String ISSUER = "gout-backend";
    private static final String ROLES_CLAIM = "roles";

    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpriredInSecond;
    private final long refreshTokenExpriredInSecond;


    public TokenService(
        JwtEncoder jwtEncoder, 
            @Value("${token.access-token-expired-in-seconds}") long accessTokenExpiredInSeconds,
            @Value("${token.refresh-token-expired-in-seconds}") long refreshTokenExpiredInSeconds)  {
        this.accessTokenExpriredInSecond = accessTokenExpiredInSeconds;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenExpriredInSecond = refreshTokenExpiredInSeconds;
    }


    public String issueAccessToken(Authentication auth, Instant issueDate) {
        return generateToken(auth, issueDate, accessTokenExpriredInSecond);
    }

    public String issueRefreshToken(Authentication auth, Instant issueDate) {
        return generateToken(auth, issueDate, refreshTokenExpriredInSecond);
    }


    // งงจัดเลอ
    public String generateToken(Authentication auth, Instant issueDate, long expiredInString) {
        Instant expire = issueDate.plusSeconds(expiredInString);

        String scope = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        var authenticatedUser = (AuthenticatedUser) auth.getPrincipal();   
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            .issuedAt(issueDate)
            .subject(String.valueOf(authenticatedUser.userId()))
            .claim(ROLES_CLAIM, scope)
            .expiresAt(expire)
            .build();

        return endcodeClaimToJwt(claims);
    }

    public String endcodeClaimToJwt(JwtClaimsSet claims){
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
