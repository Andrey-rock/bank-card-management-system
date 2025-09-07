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

    //Тест успешной регистрации
    @Test
    public void testRegisterSuccess() {
        String username = "test";
        String password = "password";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Register register = new Register(username, password, Role.USER);
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("test_token");
        when(manager.userExists(username)).thenReturn(false);
        when(encoder.encode(password)).thenReturn(encodedPassword);
        when(userMapper.entityFromRegister(register)).thenReturn(user);
        when(jwtService.generateToken(new SecurityUser(user))).thenReturn(jwtAuthenticationResponse.getToken());

        JwtAuthenticationResponse response = authService.register(register);

        verify(encoder).encode(register.getPassword());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(manager).createUser(userCaptor.capture());
        User createdUser = userCaptor.getValue();
        assertEquals("test", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        Assertions.assertEquals(response, jwtAuthenticationResponse);
    }

    @Test
    public void testRegisterFailedBecauseUserExists() {
        String username = "test";
        String password = "password";
        Register register = new Register(username, password, Role.USER);
        when(manager.userExists(username)).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> authService.register(register));

        verify(manager).userExists(register.getUsername());
        verifyNoMoreInteractions(userMapper, encoder, manager, jwtService);
    }

    @Test
    void testLoginSuccess() {
        String username = "username";
        String password = "password";
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("token");
        UserDetails userDetails = mock(UserDetails.class);
        when(manager.userExists(username)).thenReturn(true);
        when(manager.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("hashedPassword");
        when(encoder.matches(password, "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn(jwtResponse.getToken());

        JwtAuthenticationResponse result = authService.login(username, password);

        Assertions.assertEquals(result, jwtResponse);
        verify(manager).userExists(username);
        verify(manager).loadUserByUsername(username);
        verify(encoder).matches(password, "hashedPassword");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void testLoginFailedBecausePasswordWrong() {
        String username = "username";
        String password = "password";
        UserDetails userDetails = mock(UserDetails.class);
        when(manager.userExists(username)).thenReturn(true);
        when(manager.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("hashedPassword");
        when(encoder.matches(password, "hashedPassword")).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> authService.login(username, password));
        verify(manager).userExists(username);
        verify(manager).loadUserByUsername(username);
        verify(encoder).matches(password, "hashedPassword");
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testLoginFailedBecauseUserNotExists() {
        String username = "username";
        String password = "password";
        when(manager.userExists(username)).thenReturn(false);

        assertThrows(UserNoSuchException.class, () -> authService.login(username, password));
        verifyNoMoreInteractions(encoder, manager, jwtService);
    }
}
