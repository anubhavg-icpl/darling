package com.example.backend.exception;

public class InvalidOperationException extends BaseAppException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
