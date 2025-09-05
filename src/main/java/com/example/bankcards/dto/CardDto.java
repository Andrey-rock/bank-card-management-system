package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private UUID id;
    private String cardNumber;
    private LocalDate expiryDate;
    private Status status;
    private BigDecimal balance;
    private String ownerName;
}
