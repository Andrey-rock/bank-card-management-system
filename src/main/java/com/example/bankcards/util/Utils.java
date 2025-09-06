package com.example.bankcards.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class Utils {

    public String transformNumber(String number) {
        return number.replaceAll(" ", "");
    }

    @NotNull
    String mask(@NotNull String cardNumber) {
        if (cardNumber.length() < 8) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
