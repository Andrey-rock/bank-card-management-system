package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO c информацией о пользователе.
 *
 * @author Andrei Bronskijj
 * @version 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "информация о пользователе")
public class UserDto {

    @NotNull
    @Min(1)
    @Schema(description = "идентификатор пользователя", minimum = "1")
    private Long id;
    @Size(min = 4, max = 32)
    @NotBlank
    @Schema(description = "имя пользователя", minLength = 4,maxLength = 32)
    private String username;
    @Schema(description = "роль")
    private Role role;
    @Schema(description = "статус пользователя")
    private boolean enabled;
}
