package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для регистрации пользователя.
 *
 * @author Andrei Bronskijj
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Форма регистрации")
public class Register {

    @Size(min = 4, max = 32)
    @NotBlank
    @Schema(description = "логин", minLength = 4,maxLength = 32)
    private String username;
    @Size(min = 8, max = 16)
    @NotBlank
    @Schema(description = "пароль", minLength = 8,maxLength = 16)
    private String password;
    @NotNull
    @Schema(description = "роль пользователя")
    private Role role;
}
