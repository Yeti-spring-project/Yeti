package com.example.yetiproject.exception.entity.user;

public class UserUnauthorizedException extends RuntimeException{
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
