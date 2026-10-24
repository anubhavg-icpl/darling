package com.example.backend.exception;

public class UnauthorizedException extends BaseAppException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
