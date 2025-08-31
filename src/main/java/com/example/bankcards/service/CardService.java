package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;
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

        User user = userRepository.findById(userId).orElseThrow(UserNoSuchException::new);
        Card card = new Card();
        card.setCardNumber(getNewNumber());
        card.setOwner(user);
        card.setStatus(Status.ACTIVE);
        card.setExpiryDate(LocalDate.now(Clock.systemDefaultZone()).plusYears(5));
        card.setBalance(BigDecimal.ZERO);


        Card savedCard = cardRepository.save(card);
        return cardMapper.toCardDto(savedCard);
    }

    public Collection<CardDto> findAll() {
        return cardRepository.findAll().stream().map(cardMapper::toCardDto).toList();
    }

    public CardDto findById(UUID id) {
        return cardMapper.toCardDto(cardRepository.findById(id).orElseThrow(CardNoSuchException::new));
    }

    public void delete(UUID id) {
        cardRepository.deleteById(id);
    }

    public void setStatus(UUID id, String status) {
        Card card = cardRepository.findById(id).orElseThrow(CardNoSuchException::new);
        card.setStatus(Status.valueOf(status));
        cardRepository.save(card);
    }

    public Card update(Card card) {
        return cardRepository.save(card);
    }

    private long getNewNumber() {
        Random random = new Random();
        return random.nextLong(1000000000000000L, 9999999999999999L);
    }
}
