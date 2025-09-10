package com.example.bankcards.controller;

import com.example.bankcards.dto.CardUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.UserMapperImpl;
import com.example.bankcards.util.Utils;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN")
public class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CardRepository cardRepository;

    @MockBean
    UserRepository userRepository;

    @SpyBean
    CardService cardService;

    @SpyBean
    UserService userService;

    @SpyBean
    UserMapperImpl userMapper;

    @SpyBean
    CardMapper cardMapper;

    @SpyBean
    JwtService jwtService;

    @SpyBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @SpyBean
    Utils utils;

    private final UUID testCardId = UUID.randomUUID();
    private final Long testUserId = 1L;
    private final User user = new User(testUserId, "username", "password", Role.ADMIN, true, List.of(new Card()));
    private final User user2 = new User(2L, "username2", "password2", Role.USER, true, List.of(new Card()));
    private final Card card1 = new Card(testCardId, "number1", LocalDate.now(Clock.systemDefaultZone()),
            Status.ACTIVE, BigDecimal.ZERO, user);
    private final Card card2 = new Card(UUID.randomUUID(), "0000000000001234", LocalDate.now(Clock.systemDefaultZone()),
            Status.BLOCKED, BigDecimal.TEN, user);

    @Test
    public void getAllCardsTest() throws Exception {
        // Given
        List<Card> cards = List.of(card1, card2);
        when(cardRepository.findAll()).thenReturn(cards);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/cards")//
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardNumber").value("****"))
                .andExpect(jsonPath("$[0].expiryDate").value(LocalDate.now(Clock.systemDefaultZone()).toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].balance").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$[0].ownerName").value("username"))
                .andExpect(jsonPath("$[1].cardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$[1].status").value("BLOCKED"))
                .andExpect(jsonPath("$[1].balance").value(BigDecimal.TEN));
    }

    @Test
    void updateCard_WithValidData_ShouldReturnUpdatedCard() throws Exception {
        // Given
        CardUpdateDto updateDto = new CardUpdateDto(
                testCardId,
                "1234567890123456",
                LocalDate.now(),
                Status.ACTIVE,
                BigDecimal.valueOf(1500.50)
        );

        when(cardRepository.findById(testCardId)).thenReturn(Optional.of(card1));
        when(cardRepository.save(card1)).thenReturn(card1);

        JSONObject object = new JSONObject();
        object.put("id", testCardId);
        object.put("cardNumber", updateDto.getCardNumber());
        object.put("expiryDate", updateDto.getExpiryDate());
        object.put("status", updateDto.getStatus());
        object.put("balance", updateDto.getBalance());

        // When & Then
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCardId.toString()))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 3456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(1500.50))
                .andExpect(jsonPath("$.ownerName").value("username"));
        verify(cardRepository).findById(card1.getCardId());
        verify(cardRepository, times(2)).save(card1);
    }

    @Test
    void updateCard_WithNullCardNumber_ShouldReturnBadRequest() throws Exception {
        // Given - карта без номера (невалидно)
        CardUpdateDto invalidDto = new CardUpdateDto(
                testCardId,
                null, // null card number - должно вызвать валидацию
                LocalDate.now().plusYears(3),
                Status.ACTIVE,
                new BigDecimal("1000.00")
        );

        when(cardRepository.findById(testCardId)).thenReturn(Optional.of(card1));

        JSONObject object = new JSONObject();
        object.put("cardNumber", invalidDto.getCardNumber());
        object.put("expiryDate", invalidDto.getExpiryDate());
        object.put("status", invalidDto.getStatus());
        object.put("balance", invalidDto.getBalance());

        // When & Then
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateCard_WithEmptyCardNumber_ShouldReturnBadRequest() throws Exception {
        // Given - пустой номер карты
        CardUpdateDto invalidDto = new CardUpdateDto(
                testCardId,
                "", // empty card number
                LocalDate.now().plusYears(3),
                Status.ACTIVE,
                new BigDecimal("1000.00")
        );
        JSONObject object = new JSONObject();
        object.put("cardNumber", invalidDto.getCardNumber());
        object.put("expiryDate", invalidDto.getExpiryDate());
        object.put("status", invalidDto.getStatus());
        object.put("balance", invalidDto.getBalance());
        object.put("ownerName", "username");

        // When & Then
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).update(any());
    }

    @Test
    void updateCard_WithNegativeBalance_ShouldReturnBadRequest() throws Exception {
        // Given - отрицательный баланс
        CardUpdateDto invalidDto = new CardUpdateDto(
                testCardId,
                "1234567890123456",
                LocalDate.now().plusYears(3),
                Status.ACTIVE,
                new BigDecimal("-100.00") // negative balance
        );
        JSONObject object = new JSONObject();
        object.put("cardNumber", invalidDto.getCardNumber());
        object.put("expiryDate", invalidDto.getExpiryDate());
        object.put("status", invalidDto.getStatus());
        object.put("balance", invalidDto.getBalance());
        object.put("ownerName", "username");

        // When & Then
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).update(any());
    }


    @Test
    void updateCard_WithoutCsrfToken_ShouldReturnForbidden() throws Exception {
        // Given
        CardUpdateDto updateDto = new CardUpdateDto(
                testCardId,
                "1234567890123456",
                LocalDate.now().plusYears(3),
                Status.ACTIVE,
                new BigDecimal("1000.00")
        );
        JSONObject object = new JSONObject();
        object.put("cardNumber", updateDto.getCardNumber());
        object.put("expiryDate", updateDto.getExpiryDate());
        object.put("status", updateDto.getStatus());
        object.put("balance", updateDto.getBalance());
        object.put("ownerName", "username");

        // When & Then - без CSRF токена
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object)))
                .andExpect(status().isForbidden());

        verify(cardService, never()).update(any());
    }

    @Test
    void updateCard_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
        // Given - некорректный JSON
        String malformedJson = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"cardNumber\": ";

        // When & Then
        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).update(any());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        // Given
        List<User> users = List.of(user, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/users")//
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("username"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("username2"))
                .andExpect(jsonPath("$[1].role").value("USER"));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                1L,
                "username",
                Role.ADMIN,
                true
        );

        when(userRepository.existsById(userDto.getId())).thenReturn(true);
        when(userRepository.getReferenceById(userDto.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        JSONObject object = new JSONObject();
        object.put("id", testUserId);
        object.put("username", userDto.getUsername());
        object.put("role", userDto.getRole().toString());
        object.put("enabled", userDto.isEnabled());

        // When & Then
        mockMvc.perform(put("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value("true"));

        verify(userRepository).getReferenceById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_WithTooShortName_ShouldReturnBadRequest() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                1L,
                "qwe",
                Role.ADMIN,
                true
        );

        JSONObject object = new JSONObject();
        object.put("id", testUserId);
        object.put("username", userDto.getUsername());
        object.put("role", userDto.getRole().toString());
        object.put("enabled", userDto.isEnabled());

        // When & Then
        mockMvc.perform(put("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).getReferenceById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WithInvalidRole_ShouldReturnBadRequest() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                1L,
                "username",
                Role.ADMIN,
                true
        );

        JSONObject object = new JSONObject();
        object.put("id", testUserId);
        object.put("username", userDto.getUsername());
        object.put("role", "invalidRole");
        object.put("enabled", userDto.isEnabled());

        // When & Then
        mockMvc.perform(put("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).getReferenceById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_IfUserNotExsist_ShouldReturnNotFound() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                1L,
                "username",
                Role.ADMIN,
                true
        );

        JSONObject object = new JSONObject();
        object.put("id", testUserId);
        object.put("username", userDto.getUsername());
        object.put("role", userDto.getRole().toString());
        object.put("enabled", userDto.isEnabled());

        when(userRepository.existsById(userDto.getId())).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userRepository, never()).getReferenceById(any());
        verify(userRepository, never()).save(any());
    }
}
