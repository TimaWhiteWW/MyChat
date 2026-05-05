package com.TAstanov.MyChat_Auth.domain.exception;

public class PasswordMismatch extends RuntimeException {
    public PasswordMismatch(String message) {
        super(message);
    }
}
