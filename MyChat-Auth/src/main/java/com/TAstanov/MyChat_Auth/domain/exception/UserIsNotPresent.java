package com.TAstanov.MyChat_Auth.domain.exception;

public class UserIsNotPresent extends RuntimeException {
    public UserIsNotPresent(String message) {
        super(message);
    }
}
