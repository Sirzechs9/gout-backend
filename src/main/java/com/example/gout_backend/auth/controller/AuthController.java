package com.example.gout_backend.auth.controller;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.gout_backend.auth.dto.LogOutDto;
import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.service.AuthService;


@RestController


@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginRequestDto body) {
        
        return ResponseEntity.ok(authService.login(body)); 
    }

    @PostMapping("/logout")
    public ResponseEntity<?> postMethodName(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var logoutDto = new LogOutDto(jwt.getClaimAsString("sub"), jwt.getClaimAsString("role"));
        authService.logout(logoutDto);
        return ResponseEntity.noContent().build();  
    }
    
    
}
