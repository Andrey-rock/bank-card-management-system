package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final CardService cardService;

    public AdminController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/cards")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CardDto> getAllCards() {
        return cardService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    public CardDto getCardById(@PathVariable String id) {
        return cardService.findById(UUID.fromString(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public CardDto getCardByNumber(@RequestParam(name = "number") String number) {
        return cardService.findByCardNumber(number);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void deleteCard(@PathVariable String id) {
        cardService.delete(UUID.fromString(id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    public void deleteCardByNumber(@RequestParam String number) {
        cardService.deleteByCardNumber(number);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CardDto createCard(Long userId) {
        return cardService.create(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping({"{number}"})
    public CardDto setStatus(@PathVariable String number, @RequestParam(name = "status") String status) {
        return cardService.setStatus(number, status);
    }

    @PutMapping
    public CardDto updateCard(CardUpdateDto card) {
        return cardService.update(card);
    }
}
