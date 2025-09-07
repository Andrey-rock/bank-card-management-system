package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для аутентификации пользователя.
 *
 * @author Andrei Bronskijj
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Форма аутентификации")
public class Login {

    @Size(min = 4, max = 32)
    @NotBlank
    @Schema(description = "логин", minLength = 4,maxLength = 32)
    private String username;
    @Size(min = 8, max = 16)
    @NotBlank
    @Schema(description = "пароль", minLength = 8,maxLength = 16)
    private String password;
}
