package com.example.bankcards.security;

import com.example.bankcards.entity.SecurityUser;
import com.example.bankcards.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
//    private final PasswordEncoder encoder;

    public MyUserDetailsService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
//        this.encoder = encoder;
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

        SecurityUser securityUser = new SecurityUser(userRepository.findByUsername(username));
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

        return userRepository.findByUsername(userName) != null;
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

    /**
     * Метод для обновления пароля пользователя
     *
     * @param username    - логин пользователя
     * @param newPassword - новый пароль
     * @return boolean
     */
//    public boolean changePassword(String username, @NotNull NewPassword newPassword) {
//
//        log.debug("Method for change password start");
//        User user = userRepository.findByUsername(username);
//        String currentPass = newPassword.getCurrentPassword();
//        String newPass = newPassword.getNewPassword();
//        if (encoder.matches(currentPass, userEntity.getPassword())) {
//            userEntity.setPassword(encoder.encode(newPass));
//            userRepository.save(userEntity);
//            log.debug("Method for change password successfully completed");
//            return true;
//        }
//        log.debug("Method for change password fail");
//        return false;
//    }
}
