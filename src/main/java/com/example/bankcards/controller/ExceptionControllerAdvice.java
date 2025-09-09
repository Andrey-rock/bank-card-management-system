package com.example.bankcards.controller;

import com.example.bankcards.dto.BankError;
import com.example.bankcards.exception.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Hidden
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(CardNoSuchException.class)
    public ResponseEntity<BankError> handleCardNoSuchException(@NotNull CardNoSuchException e) {
        BankError bankError = new BankError("404", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bankError);
    }

    @ExceptionHandler(UserNoSuchException.class)
    public ResponseEntity<BankError> handleUserNoSuchException(@NotNull UserNoSuchException e) {
        BankError bankError = new BankError("404", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bankError);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<BankError> handleWrongPasswordException(@NotNull WrongPasswordException e) {
        BankError bankError = new BankError("400", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<BankError> handleUserAlreadyExistException(@NotNull UserAlreadyExistException e) {
        BankError bankError = new BankError("400", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BankError> handleMethodArgumentNotValidException() {
        BankError bankError = new BankError("400", "Неверный формат данных");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BankError> handleHttpMessageNotReadableException(@NotNull HttpMessageNotReadableException e) {
        BankError bankError = new BankError("400", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }

    @ExceptionHandler(MyIllegalArgumentException.class)
    public ResponseEntity<BankError> handleMyIllegalArgumentException(@NotNull MyIllegalArgumentException e) {
        BankError bankError = new BankError("400", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BankError> handleIllegalArgumentException(@NotNull IllegalArgumentException e) {
        BankError bankError = new BankError("400", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bankError);
    }
}

