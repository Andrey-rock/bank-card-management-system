package com.example.bankcards.service;

import com.example.bankcards.dto.Register;
import com.example.bankcards.entity.SecurityUser;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserDoesNotExistException;
import com.example.bankcards.exception.WrongPasswordException;
import com.example.bankcards.security.MyUserDetailsService;
import com.example.bankcards.util.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final PasswordEncoder encoder;
    private final MyUserDetailsService manager;
    private final UserMapper userMapper;

    public AuthService(PasswordEncoder passwordEncoder, MyUserDetailsService userDetailsManager, UserMapper userMapper) {

        this.encoder = passwordEncoder;
        this.manager = userDetailsManager;
        this.userMapper = userMapper;
    }

    /**
     * Метод для авторизации пользователя
     *
     * @param userName - логин пользователя
     * @param password - пароль пользователя
     * @return проверяет совпадение переданного пароля с сохраненным
     */

    public boolean login(String userName, String password) {
        log.info("Method of the login user's start");
        if (!manager.userExists(userName)) {
            log.info("User {} does not exist", userName);
            throw new UserDoesNotExistException("Пользователь " + userName + " не зарегистрирован");
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        if (!encoder.matches(password, userDetails.getPassword())) {
            log.info("User {} does not match password", userName);
            throw new WrongPasswordException();
        }
        log.info("User {} successfully logged in", userName);
        return true;
    }

    /**
     * Метод для регистрации нового пользователя
     *
     * @param register - DTO для регистрации пользователя
     * @return boolean
     */

    public boolean register(@NotNull Register register) {
        log.info("Method of the register user's start");
        if (manager.userExists(register.getUsername())) {
            log.info("User {} already exists", register.getUsername());
            throw new UserAlreadyExistException();
        }
        User user = userMapper.entityFromRegister(register);
        user.setPassword(encoder.encode(register.getPassword()));
        manager.createUser(user);
        log.info("User {} successfully registered", register.getUsername());
        return true;
    }
}
