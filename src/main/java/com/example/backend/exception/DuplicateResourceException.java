package com.example.backend.exception;

public class DuplicateResourceException extends BaseAppException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
