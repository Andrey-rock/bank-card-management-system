package com.example.bankcards.exception;

public class UserNoSuchException extends RuntimeException {
    public UserNoSuchException() {
        super("Пользователь не найден");
    }
}
