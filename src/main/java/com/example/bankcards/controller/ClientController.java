package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class ClientController {

    private final UserService userService;
    private final CardService cardService;

    @Transactional
    @GetMapping()
    public Collection<CardDto> getAllCardsByName(Authentication authentication,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "3") int size,
                                                 @RequestParam(required = false) String cardNumber) {
        long userId = userService.getUserByUsername(authentication.getName()).getId();
        if (cardNumber != null) {
            return cardService.findByOwnerIdAndCardNumber(userId, cardNumber, page, size);
        }
        return cardService.findByOwnerId(userId, page, size);
    }

    @Transactional
    @PostMapping("transfer")
    public void transferMany(String cardNumber1, String cardNumber2, double amount) {
        cardService.transferMoney(cardNumber1, cardNumber2, BigDecimal.valueOf(amount));
    }


    @GetMapping("{id}/balance")
    public BigDecimal getBalance(@PathVariable UUID id) {
        return cardService.getBalance(id);
    }

    @PostMapping("block/{id}")
    public String blockingRequest(@PathVariable String id, Authentication authentication) {
        return userService.blockingRequest(id, authentication.getName());
    }
}
