package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNoSuchException::new);
    }

    public User updateUser(@NotNull User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNoSuchException();
        }
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::entityToDto).toList();
    }

    public UserDto getUserById(long id) {
        return userMapper.entityToDto(userRepository.findById(id).orElseThrow(UserNoSuchException::new));
    }

    public void blockedUser(long id) {
        User user = userRepository.findById(id).orElseThrow(UserNoSuchException::new);
        user.setEnabled(false);
    }

    public String blockingRequest(String id, String name) {
        //В коммерческом приложении сообщение должно уйти администраторам. Здесь просто печатается в консоль
        log.info("Blocking request for card id {} by user with name {}", id, name);
        return String.format("Запрос на блокировку карты id= %s от пользователя %s принят", id, name);
    }
}
