package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер администратора банка
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
@Tag(name = "Функции администратора")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @Operation(summary = "Просмотр всех карт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("cards")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CardDto> getAllCards() {
        return cardService.findAll();
    }

    @Operation(summary = "Получение карты по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    public CardDto getCardById(@PathVariable String id) {
        return cardService.findById(UUID.fromString(id));
    }

    @Operation(summary = "Удаление карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void deleteCard(@PathVariable String id) {
        cardService.delete(UUID.fromString(id));
    }

    @Operation(summary = "Создание карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{userId}")
    public CardDto createCard(@PathVariable long userId) {
        return cardService.create(userId);
    }

    @Operation(summary = "Смена статуса карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping({"{id}"})
    public CardDto setStatus(@PathVariable String id, @RequestParam(name = "status") @Valid String status) {
        return cardService.setStatus(id, status);
    }

    @Operation(summary = "Смена данных карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @PutMapping
    public CardDto updateCard(@Valid @RequestBody CardUpdateDto card) {
        return cardService.update(card);
    }

    @Operation(summary = "Блокировка пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("user/block/{id}")
    public void blockedUser(@PathVariable long id) {
        userService.blockedUser(id);
    }

    @Operation(summary = "Смена данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("user")
    public UserDto updateUser(@Valid @RequestBody UserDto user) {
        return userService.updateUser(user);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Просмотр всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("users")
    public Collection<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("user/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }
}
