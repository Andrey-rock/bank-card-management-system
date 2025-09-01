package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для регистрации пользователя.
 *
 * @author skypro-backend
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Register {

    @Schema(description = "логин", minLength = 4,maxLength = 32)
    private String username;
    @Schema(description = "пароль", minLength = 8,maxLength = 16)
    private String password;
    @Schema(description = "роль пользователя")
    private Role role;
}
