package com.example.gout_backend.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public LoginResponseDto login(@RequestBody @Validated LoginRequestDto body) {
        
        return authService.login(body);
    }
    
}
