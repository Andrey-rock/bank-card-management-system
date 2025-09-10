package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

/**
 * Сервис для управления пользователями.
 * <p>
 * Предоставляет методы для работы с данными пользователей, включая получение,
 * обновление, удаление и блокировку пользователей, а также обработку запросов на блокировку карт.
 * </p>
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return найденный пользователь
     * @throws UserNoSuchException если пользователь с указанным именем не найден
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNoSuchException::new);
    }

    /**
     * Обновляет данные пользователя.
     * <p>
     * Метод выполняется в транзакционном контексте для обеспечения целостности данных.
     * </p>
     *
     * @param user DTO с обновленными данными пользователя
     * @return DTO обновленного пользователя
     * @throws UserNoSuchException если пользователь с указанным ID не существует
     */
    @Transactional
    public UserDto updateUser(@NotNull UserDto user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNoSuchException();
        }
        User user1 = userRepository.getReferenceById(user.getId());
        user1.setUsername(user.getUsername());
        user1.setRole(user.getRole());
        user1.setEnabled(user.isEnabled());
        return userMapper.entityToDto(userRepository.save(user1));
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     */
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return коллекция DTO всех пользователей
     */
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::entityToDto).toList();
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws UserNoSuchException если пользователь с указанным ID не найден
     */
    public UserDto getUserById(long id) {
        return userMapper.entityToDto(userRepository.findById(id).orElseThrow(UserNoSuchException::new));
    }

    /**
     * Блокирует пользователя (устанавливает флаг enabled в false).
     *
     * @param id идентификатор пользователя для блокировки
     * @throws UserNoSuchException если пользователь с указанным ID не найден
     */
    public void blockedUser(long id) {
        User user = userRepository.findById(id).orElseThrow(UserNoSuchException::new);
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Обрабатывает запрос на блокировку карты.
     * <p>
     * В реальном приложении запрос должен отправляться администраторам.
     * В данной реализации информация печатается в консоль.
     * </p>
     *
     * @param id идентификатор карты для блокировки (в виде строки)
     * @param name имя пользователя, отправившего запрос
     * @return сообщение о принятии запроса на блокировку
     */
    public String blockingRequest(String id, String name) {
        UUID uuid = UUID.fromString(id);

        log.info("Blocking request for card id {} by user with name {}", id, name);
        return String.format("Запрос на блокировку карты id= %s от пользователя %s принят", uuid, name);
    }
}
