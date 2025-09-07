package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для изменения данных карты.
 *
 * @author Andrei Bronskijj
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о карте для изменения данных")
public class CardUpdateDto {

    @Schema(description = "идентификатор карты", example = "00000000-0000-0000-0000-000000000000")
    private UUID id;
    @NotBlank
    @Schema(description = "номер карты", example = "1111 2222 3333 4444")
    private String cardNumber;
    @Pattern(regexp = "[0-9]{4}/[0-9]{2}/[0-9]{2}")
    @Schema(description = "Срок действия", format = "date")
    private LocalDate expiryDate;
    @NotBlank
    @Schema(description = "статус")
    private Status status;
    @Min(0)
    @Schema(description = "баланс карты", minimum = "0")
    private BigDecimal balance;
}
