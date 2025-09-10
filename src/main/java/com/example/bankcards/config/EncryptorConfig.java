package com.example.bankcards.config;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Конфигурация шифратора номеров карт
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class EncryptorConfig {

    @Value("${encryption.secret.key}")
    private String secretKey;

    @Bean
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPoolSize(4);
        encryptor.setPassword(secretKey);
        encryptor.setAlgorithm("PBEWithSHA256And256BitAES-CBC-BC");
        return encryptor;
    }

}
