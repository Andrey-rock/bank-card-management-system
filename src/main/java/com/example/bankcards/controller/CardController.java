package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;

import com.example.bankcards.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CardDto> getAllCards() {
        return cardService.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable String id) {
        CardDto card = cardService.findById(UUID.fromString(id));
        if (card == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCard(@PathVariable String id) {
        CardDto card = cardService.findById(UUID.fromString(id));
        if (card == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        cardService.delete(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public CardDto createCard(Long userId) {
        return cardService.create(userId);
    }
}
