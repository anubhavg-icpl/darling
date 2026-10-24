package com.example.backend.exception;

public class BadRequestException extends BaseAppException {
    public BadRequestException(String message) {
        super(message);
    }
}
