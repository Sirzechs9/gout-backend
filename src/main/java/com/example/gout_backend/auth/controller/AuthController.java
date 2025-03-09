package com.example.gout_backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gout_backend.auth.dto.LoginRequestDto;
import com.example.gout_backend.auth.dto.LoginResponseDto;
import com.example.gout_backend.auth.dto.LogoutDto;
import com.example.gout_backend.auth.dto.RefreshTokenDto;
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

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody @Validated RefreshTokenDto body) {
        return ResponseEntity.ok(authService.issueNewRefreshToken(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var logoutDto = new LogoutDto(jwt.getClaimAsString("sub"), jwt.getClaimAsString("roles"));
        System.out.println(String.valueOf(logoutDto.roles()));
        System.out.println(String.valueOf(logoutDto.sub()));
        authService.logout(logoutDto);
        return ResponseEntity.noContent().build();
    }
    
}


