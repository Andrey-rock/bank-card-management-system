package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO с информацией о карте.
 *
 * @author Andrei Bronskijj
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ответ c информацией о карте")
public class CardDto {

    @Schema(description = "идентификатор карты")
    private UUID id;
    @Schema(description = "номер карты", example = "**** **** **** 1234")
    private String cardNumber;
    @Schema(description = "срок действия", format = "date")
    private LocalDate expiryDate;
    @Schema(description = "статус")
    private Status status;
    @Schema(description = "баланс карты")
    private BigDecimal balance;
    @Schema(description = "владелец карты")
    private String ownerName;
}
