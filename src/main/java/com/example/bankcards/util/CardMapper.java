package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.repository.CardRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    private final CardRepository cardRepository;
    private final Utils utils;

    public CardMapper(CardRepository cardRepository, Utils utils) {
        this.cardRepository = cardRepository;
        this.utils = utils;
    }

    public CardDto toCardDto(@NotNull Card card) {
        CardDto cardDto = new CardDto();
        String cardNumber = Long.toString(card.getCardNumber());
        cardDto.setCardNumber(utils.mask(cardNumber));
        cardDto.setOwnerName(card.getOwner().getUsername());
        cardDto.setStatus(card.getStatus());
        cardDto.setBalance(card.getBalance());
        cardDto.setExpiryDate(card.getExpiryDate());
        return cardDto;
    }

    public Card toEntity(@NotNull CardUpdateDto card) {
        Card card1 = cardRepository.findById(card.getId()).orElseThrow(CardNoSuchException::new);
        card1.setCardNumber(utils.transformNumber(card.getCardNumber()));
        card1.setExpiryDate(card.getExpiryDate());
        card1.setBalance(card.getBalance());
        card1.setStatus(card.getStatus());
        return cardRepository.save(card1);
    }
}
