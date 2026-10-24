package com.example.backend.exception;

public class TokenExpiredException extends BaseAppException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
