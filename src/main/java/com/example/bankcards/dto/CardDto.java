package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private String cardNumber;
    private LocalDate expiryDate;
    private Status status;
    private BigDecimal balance;
    private User owner;
}
