package com.example.bankcards.service;

import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.Register;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.SecurityUser;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserNoSuchException;
import com.example.bankcards.exception.WrongPasswordException;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.MyUserDetailsService;
import com.example.bankcards.util.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Тестирование AuthService
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private MyUserDetailsService manager;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    String username = "test";
    String password = "password";
    String encodedPassword = "encodedPassword";
    User user = new User();
    Register register = new Register(username, password, Role.USER);

    @BeforeEach
    void setUp() {
        user.setUsername(username);
        user.setPassword(password);
    }


    //Тест успешной регистрации
    @Test
    public void testRegisterSuccess() {

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("token");
        when(manager.userExists(username)).thenReturn(false);
        when(encoder.encode(password)).thenReturn(encodedPassword);
        when(userMapper.entityFromRegister(register)).thenReturn(user);
        when(jwtService.generateToken(new SecurityUser(user))).thenReturn(jwtAuthenticationResponse.getToken());

        JwtAuthenticationResponse response = authService.register(register);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(manager).createUser(userCaptor.capture());
        User createdUser = userCaptor.getValue();
        assertEquals("test", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        Assertions.assertEquals(response, jwtAuthenticationResponse);
    }

    //Тест провальной регистрации - пользователь уже существует
    @Test
    public void testRegisterFailedBecauseUserExists() {

        when(manager.userExists(username)).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> authService.register(register));

        verifyNoMoreInteractions(userMapper, encoder, manager, jwtService);
    }

    //Тест успешной аутентификации
    @Test
    void testLoginSuccess() {

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("token");
        UserDetails userDetails = mock(UserDetails.class);
        when(manager.userExists(username)).thenReturn(true);
        when(manager.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("hashedPassword");
        when(encoder.matches(password, "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn(jwtResponse.getToken());

        JwtAuthenticationResponse result = authService.login(username, password);

        Assertions.assertEquals(result, jwtResponse);
    }

    //Тест провальной аутентификации - неверный пароль
    @Test
    void testLoginFailedBecausePasswordWrong() {

        UserDetails userDetails = mock(UserDetails.class);
        when(manager.userExists(username)).thenReturn(true);
        when(manager.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("hashedPassword");
        when(encoder.matches(password, "hashedPassword")).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> authService.login(username, password));
    }

    //Тест провальной аутентификации - пользователь не найден
    @Test
    void testLoginFailedBecauseUserNotExists() {

        when(manager.userExists(username)).thenReturn(false);

        assertThrows(UserNoSuchException.class, () -> authService.login(username, password));
        verifyNoMoreInteractions(encoder, manager, jwtService);
    }
}
