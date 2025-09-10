package com.example.bankcards.security;

import com.example.bankcards.entity.SecurityUser;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация UserDetailsService для работы с пользователями в контексте Security
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод для получения данных пользователя по его логину
     *
     * @param username - логин пользователя
     * @return данные пользователя
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Method for loading user's by username start");

        SecurityUser securityUser = new SecurityUser(userRepository.findByUsername(username).orElseThrow(UserNoSuchException::new));
        return new User(securityUser.getUsername(), securityUser.getPassword(),
                securityUser.getAuthorities());
    }

    /**
     * Метод для проверки существования пользователя
     *
     * @param userName - логин пользователя
     * @return boolean
     */
    public boolean userExists(String userName) {

        log.debug("Method for checking existing user start");

        return userRepository.findByUsername(userName).isPresent();
    }

    /**
     * Метод для создания нового пользователя
     *
     * @param user - Entity для пользователей.
     */
    public void createUser(com.example.bankcards.entity.User user) {

        log.info("Method for create new user start");

        userRepository.save(user);
    }
}
