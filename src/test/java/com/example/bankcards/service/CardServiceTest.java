package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.exception.MyIllegalArgumentException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестирование CardService
 * <p>
 * Проверяется наиболее критичный метод - transferMoney
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private Utils utils;

    @InjectMocks
    private CardService cardService;

    User user = new User();
    Card card1 = new Card();
    Card card2 = new Card();

    String cardNumber1 = "1234567890123456";
    String cardNumber2 = "9876543210987654";

    UUID uuid1;
    UUID uuid2;

    @BeforeEach
    void setUp() {

        card1.setCardId(UUID.randomUUID());
        card1.setCardNumber(cardNumber1);
        card1.setOwner(user);
        card1.setStatus(Status.ACTIVE);
        card1.setExpiryDate(LocalDate.now(Clock.systemDefaultZone()).plusYears(5));
        card1.setBalance(BigDecimal.ZERO);

        card2.setCardId(UUID.randomUUID());
        card2.setCardNumber(cardNumber2);
        card2.setOwner(user);
        card2.setStatus(Status.ACTIVE);
        card2.setExpiryDate(LocalDate.now(Clock.systemDefaultZone()));
        card2.setBalance(BigDecimal.valueOf(1000));

        uuid1 = card1.getCardId();
        uuid2 = card2.getCardId();
    }

    @Test
    public void transferMoney_ShouldTransferSuccessfully_WhenValidData() {

        when(cardRepository.findById(uuid1)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(uuid2)).thenReturn(Optional.of(card2));

        cardService.transferMoney(uuid2.toString(), uuid1.toString(), BigDecimal.valueOf(400.89));

        Assertions.assertEquals(BigDecimal.valueOf(400.89), card1.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(599.11), card2.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(cardRepository).save(card1);
        verify(cardRepository).save(card2);
    }

    @Test
    public void transferMoney_ShouldThrowCardNoSuchException_WhenFirstCardNotFound() {

        when(cardRepository.findById(uuid1)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNoSuchException.class, () -> cardService.transferMoney(uuid1.toString(), uuid2.toString(), BigDecimal.valueOf(400)));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferMoney_ShouldThrowCardNoSuchException_WhenSecondCardNotFound() {

        when(cardRepository.findById(uuid1)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(uuid2)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNoSuchException.class,
                () -> cardService.transferMoney(uuid1.toString(), uuid2.toString(), BigDecimal.valueOf(400)));

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferMoney_ShouldThrowMyIllegalArgumentException_WhenAmountIsZero() {

        MyIllegalArgumentException e = Assertions.assertThrows(MyIllegalArgumentException.class,
                () -> cardService.transferMoney(uuid1.toString(), uuid2.toString(), BigDecimal.ZERO));

        verify(cardRepository, never()).save(any(Card.class));
        verify(utils, never()).transformNumber(anyString());
        Assertions.assertEquals("Введите сумму больше нуля", e.getMessage());
    }

    @Test
    void transferMoney_ShouldThrowMyIllegalArgumentException_WhenInsufficientFunds() {
        when(cardRepository.findById(uuid1)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(uuid2)).thenReturn(Optional.of(card2));

        MyIllegalArgumentException e = Assertions.assertThrows(MyIllegalArgumentException.class,
                () -> cardService.transferMoney(uuid1.toString(), uuid2.toString(), BigDecimal.valueOf(1400)));

        verify(cardRepository, never()).save(any(Card.class));
        Assertions.assertEquals("Недостаточно денег", e.getMessage());
    }
}
