package com.example.bankcards.util;

import com.example.bankcards.dto.Register;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper для пользователей.
 *
 * @author Lada Kozlova, 2025
 * @version 0.0.1
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "enabled", constant = "true")
    User entityFromRegister(Register register);

    UserDto entityToDto(User user);
}

