package com.example.yetiproject.exception.entity.user;

public class UserDuplicatedException extends RuntimeException{
    public UserDuplicatedException(String message) {
        super(message);
    }
}
