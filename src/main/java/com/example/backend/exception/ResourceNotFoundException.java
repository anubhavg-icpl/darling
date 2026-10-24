package com.example.backend.exception;

public class ResourceNotFoundException extends BaseAppException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
