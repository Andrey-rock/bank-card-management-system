package com.example.bankcards.exception;

public class CardNoSuchException extends RuntimeException {
    public CardNoSuchException() {
        super("Карта не найдена");
    }
}
