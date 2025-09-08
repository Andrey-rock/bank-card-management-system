package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер пользователя банка
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "Функции пользователя")
public class ClientController {

    private final UserService userService;
    private final CardService cardService;

    @Operation(summary = "Просмотр всех карт текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @GetMapping()
    public ResponseEntity<?> getAllCardsByName(Authentication authentication,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "3") int size,
                                                 @RequestParam(required = false) String cardNumber) {
        Collection<CardDto> response;
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        long userId = userService.getUserByUsername(authentication.getName()).getId();
        if (cardNumber != null) {
            response = cardService.findByOwnerIdAndCardNumber(userId, cardNumber, page, size);
            return ResponseEntity.ok(response);
        }
        response = cardService.findByOwnerId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Перевод денег между картами пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @PostMapping("transfer")
    public void transferMany(String cardNumber1, String cardNumber2, double amount) {
        cardService.transferMoney(cardNumber1, cardNumber2, amount);
    }


    @Operation(summary = "Просмотр баланса карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/balance")
    public BigDecimal getBalance(@PathVariable UUID id) {
        return cardService.getBalance(id);
    }

    @Operation(summary = "Просмотр баланса карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("block/{id}")
    public ResponseEntity<String> blockingRequest(@PathVariable String id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String s = userService.blockingRequest(id, authentication.getName());
        return ResponseEntity.ok(s);
    }
}
