package com.example.gout_backend.common.exception;

public class CredentialExistsException extends RuntimeException{

    public CredentialExistsException() {
        super();
    }

    public CredentialExistsException(String message) {
        super(message);
    }
}
