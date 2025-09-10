package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNoSuchException;
import com.example.bankcards.exception.MyIllegalArgumentException;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
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

/**
 * Сервис для управления банковскими картами.
 * <p>
 * Предоставляет методы для создания, поиска, обновления и удаления карт,
 * а также для выполнения операций с балансом карт.
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    /**
     * Создает новую банковскую карту для указанного пользователя.
     * <p>
     * Карта создается с уникальным номером, нулевым балансом, активным статусом
     * и сроком действия 5 лет с текущей даты.
     * </p>
     *
     * @param userId идентификатор пользователя-владельца карты
     * @return DTO созданной карты
     * @throws UserNoSuchException если пользователь с указанным ID не найден
     */
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

    /**
     * Возвращает список всех банковских карт.
     *
     * @return коллекция DTO всех карт
     */
    public Collection<CardDto> findAll() {
        return cardRepository.findAll().stream().map(cardMapper::toCardDto).toList();
    }

    /**
     * Находит карту по уникальному идентификатору.
     *
     * @param id UUID карты
     * @return DTO найденной карты
     * @throws CardNoSuchException если карта с указанным ID не найдена
     */
    public CardDto findById(UUID id) {
        return cardMapper.toCardDto(cardRepository.findById(id).orElseThrow(CardNoSuchException::new));
    }

    /**
     * Находит карты по идентификатору владельца с пагинацией.
     *
     * @param ownerId идентификатор владельца карт
     * @param page    номер страницы (начинается с 1)
     * @param size    количество элементов на странице
     * @return коллекция DTO карт указанного владельца
     */
    public Collection<CardDto> findByOwnerId(long ownerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return cardRepository.findByOwnerId(ownerId, pageRequest).getContent().stream().map(cardMapper::toCardDto).toList();
    }

    /**
     * Удаляет карту по идентификатору.
     *
     * @param id UUID карты для удаления
     * @throws CardNoSuchException если карта с указанным ID не найдена
     */
    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNoSuchException();
        }
        cardRepository.deleteById(id);
    }

    /**
     * Изменяет статус карты.
     *
     * @param id     идентификатор карты в виде строки
     * @param status новый статус карты
     * @return DTO обновленной карты
     * @throws CardNoSuchException      если карта с указанным ID не найдена
     * @throws IllegalArgumentException если передан невалидный статус
     */
    public CardDto setStatus(String id, String status) {
        Card card = cardRepository.findById(UUID.fromString(id)).orElseThrow(CardNoSuchException::new);
        card.setStatus(Status.valueOf(status));
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    /**
     * Обновляет информацию о карте.
     *
     * @param card DTO с обновленными данными карты
     * @return DTO обновленной карты
     */
    public CardDto update(CardUpdateDto card) {
        return cardMapper.toCardDto(cardRepository.save(cardMapper.toEntity(card)));
    }

    public BigDecimal getBalance(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(CardNoSuchException::new);
        return card.getBalance();
    }

    /**
     * Выполняет перевод денег между двумя картами.
     * <p>
     * Операция выполняется в транзакционном контексте для обеспечения целостности данных.
     * </p>
     *
     * @param cardId1 идентификатор карты-отправителя
     * @param cardId2 идентификатор карты-получателя
     * @param amount  сумма перевода (должна быть больше нуля)
     * @throws MyIllegalArgumentException если сумма меньше или равна нулю,
     *                                    или на карте-отправителе недостаточно средств
     * @throws CardNoSuchException        если одна из карт не найдена
     */
    @Transactional
    public void transferMoney(String cardId1, String cardId2, @NotNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MyIllegalArgumentException("Введите сумму больше нуля");
        }
        Card card1 = cardRepository.findById(UUID.fromString(cardId1)).orElseThrow(CardNoSuchException::new);
        Card card2 = cardRepository.findById(UUID.fromString(cardId2)).orElseThrow(CardNoSuchException::new);
        if (card1.getBalance().compareTo(amount) < 0) {
            throw new MyIllegalArgumentException("Недостаточно денег");
        }
        card1.setBalance(card1.getBalance().subtract(amount));
        card2.setBalance(card2.getBalance().add(amount));
        cardRepository.save(card1);
        cardRepository.save(card2);
    }

    /**
     * Генерирует новый уникальный номер карты
     * <p>
     * (при использовании в реальном банке может быть доработан, чтобы первые 8 цифр
     * соответствовали платежной системе и наименованию банка)
     *
     * @return 16-значный номер карты в виде строки
     */
    private @NotNull String getNewNumber() {
        Random random = new Random();
        return Long.toString(random.nextLong(1000000000000000L, 9999999999999999L));
    }
}
