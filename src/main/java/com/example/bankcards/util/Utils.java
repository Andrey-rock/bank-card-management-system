package com.example.bankcards.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Utils {

    public long transformNumber(String number) {
        return Long.parseLong(number.replaceAll(" ", ""));
    }

    @NotNull String mask(@NotNull String cardNumber) {
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
