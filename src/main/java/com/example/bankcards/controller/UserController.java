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

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Transactional
    @GetMapping()
    public Collection<CardDto> getAllCardsByName(Authentication authentication,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "3") int size) {
        long userId = userService.getUserByUsername(authentication.getName()).getId();
        return cardService.findByOwnerId(userId, page, size);
    }

    @Transactional
    @PostMapping("transfer")
    public void transferMany(String cardNumber1, String cardNumber2, double amount) {
        cardService.transferMoney(cardNumber1, cardNumber2, BigDecimal.valueOf(amount));
    }
}
