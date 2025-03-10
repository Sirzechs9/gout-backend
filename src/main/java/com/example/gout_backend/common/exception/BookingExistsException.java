package com.example.gout_backend.common.exception;

public class BookingExistsException extends RuntimeException {

    public BookingExistsException() {
        super();
    }

    public BookingExistsException(String message) {
        super(message);
    }
}