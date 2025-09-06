package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CardNumberEncryptor implements AttributeConverter<String, String> {

    private final StringEncryptor encryptor;

    public CardNumberEncryptor(@Qualifier("stringEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return encryptor.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting card number", e);
        }
    }
}
