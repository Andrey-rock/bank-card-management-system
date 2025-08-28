package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardDto toCardDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber(mask(card.getCardNumber()));
        cardDto.setOwner(card.getOwner());
        cardDto.setStatus(card.getStatus());
        cardDto.setBalance(card.getBalance());
        cardDto.setExpiryDate(card.getExpiryDate());
        return cardDto;
    }

    private @NotNull String mask(@NotNull String cardNumber) {
        return cardNumber.substring(0, 14).replaceAll("[0-9]", "*") +
                cardNumber.substring(14);
    }
}
