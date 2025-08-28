package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Service
public class CardService {

    private char counter = '0';

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userRepository = userRepository;
    }

    public CardDto create(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(CardNoSuchException::new);
        Card card = new Card();
        card.setCardNumber(getNewNumber());
        card.setOwner(user);
        card.setStatus(Status.ACTIVE);
        card.setExpiryDate(LocalDate.now(Clock.systemDefaultZone()).plusYears(5));
        user.getCards().add(card);

        return cardMapper.toCardDto(cardRepository.save(card));
    }

    public Collection<Card> findAll() {
        return cardRepository.findAll();
    }

    public Card findById(UUID id) {
        return cardRepository.findById(id).orElseThrow(CardNoSuchException::new);
    }

    public void delete(UUID id) {
        cardRepository.deleteById(id);
    }

    public Card setStatus(UUID id, String status) {
        Card card = findById(id);
        card.setStatus(Status.valueOf(status));
        return cardRepository.save(card);
    }

    public Card update(Card card) {
        return cardRepository.save(card);
    }

    private String getNewNumber() {
        String number = "0000 0000 0000 0000";
        StringBuilder newNumber = new StringBuilder(number);
        newNumber.setCharAt(0, ++counter);
        return newNumber.toString();
    }
}
