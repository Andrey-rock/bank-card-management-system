package com.example.bankcards.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        super("Пользователь уже существует");
    }
}
