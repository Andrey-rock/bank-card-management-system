package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNoSuchException::new);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(@NotNull User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNoSuchException();
        }
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(UserNoSuchException::new);
    }

    public void blockedUser(long id) {
        User user = getUserById(id);
        user.setEnabled(false);
    }
}
