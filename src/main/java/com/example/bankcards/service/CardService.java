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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;
    private final Utils utils;


    public CardDto create(Long userId) {
        int VALIDITY_PERIOD = 5;

        User user = userRepository.findById(userId).orElseThrow(UserNoSuchException::new);
        Card card = new Card();
        card.setCardNumber(getNewNumber());
        card.setOwner(user);
        card.setStatus(Status.ACTIVE);
        card.setExpiryDate(LocalDate.now(Clock.systemDefaultZone()).plusYears(VALIDITY_PERIOD));
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

    public Collection<CardDto> findByOwnerId(long ownerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return cardRepository.findByOwnerId(ownerId, pageRequest).getContent().stream().map(cardMapper::toCardDto).toList();
    }

    public Collection<CardDto> findByOwnerIdAndCardNumber(long ownerId,
                                                          String cardNumber,
                                                          int page,
                                                          int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return cardRepository.findByOwnerIdAndCardNumber(ownerId, utils.transformNumber(cardNumber), pageRequest).getContent()
                .stream().map(cardMapper::toCardDto).toList();
    }

    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNoSuchException();
        }
        cardRepository.deleteById(id);
    }

    public CardDto setStatus(String id, String status) {
        Card card = cardRepository.findById(UUID.fromString(id)).orElseThrow(CardNoSuchException::new);
        card.setStatus(Status.valueOf(status));
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    public CardDto update(CardUpdateDto card) {
        return cardMapper.toCardDto(cardRepository.save(cardMapper.toEntity(card)));
    }

    public BigDecimal getBalance(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(CardNoSuchException::new);
        return card.getBalance();
    }

    @Transactional
    public void transferMoney(String cardNumber1, String cardNumber2, BigDecimal amount) {
        Card card1 = cardRepository.findByCardNumber(utils.transformNumber(cardNumber1)).orElseThrow(CardNoSuchException::new);
        Card card2 = cardRepository.findByCardNumber(utils.transformNumber(cardNumber2)).orElseThrow(CardNoSuchException::new);
        card1.setBalance(card1.getBalance().subtract(amount));
        card2.setBalance(card2.getBalance().add(amount));
        cardRepository.save(card1);
        cardRepository.save(card2);
    }

    private @NotNull String getNewNumber() {
        Random random = new Random();
        return Long.toString(random.nextLong(1000000000000000L, 9999999999999999L));
    }
}
