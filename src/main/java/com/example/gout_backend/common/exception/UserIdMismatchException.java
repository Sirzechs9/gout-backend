package com.example.gout_backend.common.exception;

public class UserIdMismatchException extends RuntimeException {

    public UserIdMismatchException() {
        super();
    }

    public UserIdMismatchException(String message) {
        super(message);
    }
}
