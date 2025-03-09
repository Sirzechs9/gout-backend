package com.example.gout_backend.common.exception;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException() {
        super();
    }

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}

