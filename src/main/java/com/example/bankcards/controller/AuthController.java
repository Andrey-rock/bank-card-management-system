package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.Login;
import com.example.bankcards.dto.Register;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для регистрации и аутентификации
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Аутентификация пользователя")
    @Tag(name = "Аутентификация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public JwtAuthenticationResponse login(@Valid @RequestBody Login login) {

        log.info("Controller method's for logging user");
        return authService.login(login.getUsername(), login.getPassword());
    }

    @Operation(summary = "Регистрация пользователя")
    @Tag(name = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content())
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public JwtAuthenticationResponse register(@Valid @RequestBody Register register) {

        log.info("Controller method's for registration new user");
        return authService.register(register);
    }
}
