package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.Utils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;
    private final Utils utils;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, UserRepository userRepository, Utils utils) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.userRepository = userRepository;
        this.utils = utils;
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

    public CardDto findByCardNumber(String cardNumber) {
        return cardMapper.toCardDto(cardRepository.findByCardNumber(utils.transformNumber(cardNumber)).orElseThrow(CardNoSuchException::new));
    }

    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNoSuchException();
        }
        cardRepository.deleteById(id);
    }

    public void deleteByCardNumber(String cardNumber) {
        long number = utils.transformNumber(cardNumber);
        if (!cardRepository.existsByCardNumber(number)) {
            throw new CardNoSuchException();
        }
        cardRepository.deleteByCardNumber(number);
    }

    public CardDto setStatus(String number, String status) {
        Card card = cardRepository.findByCardNumber(utils.transformNumber(number)).orElseThrow(CardNoSuchException::new);
        card.setStatus(Status.valueOf(status));
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    public CardDto update(CardUpdateDto card) {
        return cardMapper.toCardDto(cardRepository.save(cardMapper.toEntity(card)));
    }

    private long getNewNumber() {
        Random random = new Random();
        return random.nextLong(1000000000000000L, 9999999999999999L);
    }
}
