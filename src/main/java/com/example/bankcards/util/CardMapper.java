package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardDto toCardDto(Card card) {
        CardDto cardDto = new CardDto();
        String cardNumber = Long.toString(card.getCardNumber());
        cardDto.setCardNumber(mask(cardNumber));
        cardDto.setOwnerName(card.getOwner().getUsername());
        cardDto.setStatus(card.getStatus());
        cardDto.setBalance(card.getBalance());
        cardDto.setExpiryDate(card.getExpiryDate());
        return cardDto;
    }

    private @NotNull String mask(@NotNull String cardNumber) {
        String cardNumber1 = cardNumber.substring(0, 12).replaceAll("[0-9]", "*") +
                cardNumber.substring(12);
        return cardNumber1.substring(0, 4) +
                " " +
                cardNumber1.substring(4, 8) +
                " " +
                cardNumber1.substring(8, 12) +
                " " +
                cardNumber1.substring(12, 16);
    }
}
