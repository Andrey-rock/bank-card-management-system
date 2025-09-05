package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping("cards")
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
    public CardDto findCardByNumber(@RequestParam(name = "number") String number) {
        return cardService.findByCardNumber(number);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void deleteCard(@PathVariable String id) {
        cardService.delete(UUID.fromString(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CardDto createCard(Long userId) {
        return cardService.create(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping({"{id}"})
    public CardDto setStatus(@PathVariable String id, @RequestParam(name = "status") String status) {
        return cardService.setStatus(id, status);
    }

    @PutMapping
    public CardDto updateCard(CardUpdateDto card) {
        return cardService.update(card);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("user/block/{id}")
    public void blockedUser(@PathVariable long id) {
        userService.blockedUser(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("user")
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("users")
    public Collection<UserDto> getUsers() {
       return userService.getAllUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("user")
    public UserDto getUser(@RequestParam(name = "id") long id) {
        return userService.getUserById(id);
    }
}
