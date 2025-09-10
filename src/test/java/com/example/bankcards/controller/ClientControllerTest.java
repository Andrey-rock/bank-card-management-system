package com.example.bankcards.controller;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Тестирование ClientControllerTest
 * <p>
 * Тестируется только метод getAllCardsByUser, т.к. transferMoney покрыт юнит-тестами,
 * а остальные достаточно просты.
 * </p>
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@WebMvcTest(ClientController.class)
@WithMockUser(roles = "USER")
public class ClientControllerTest {

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
    private final User user = new User(testUserId, "username", "password", Role.USER, true, List.of(new Card()));

    private final Card card1 = new Card(testCardId, "number1", LocalDate.now(Clock.systemDefaultZone()),
            Status.ACTIVE, BigDecimal.ZERO, user);
    private final Card card2 = new Card(UUID.randomUUID(), "0000000000001234", LocalDate.now(),
            Status.BLOCKED, BigDecimal.TEN, user);
    private final Card card3 = new Card(UUID.randomUUID(), "0000000000005678", LocalDate.now(),
            Status.EXPIRED, BigDecimal.ONE, user);

    @Test
    void getAllCardsByUser_WithDefaultValue_ShouldReturnCards() throws Exception {

        List<Card> cards = List.of(card1, card2, card3);
        Page<Card> cardPage = new PageImpl<>(cards);
        PageRequest pageRequest = PageRequest.of(0, 3);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerId(anyLong(), eq(pageRequest))).thenReturn(cardPage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardNumber").value("****"))
                .andExpect(jsonPath("$[0].expiryDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].balance").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$[0].ownerName").value("username"))
                .andExpect(jsonPath("$[1].cardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$[1].status").value("BLOCKED"))
                .andExpect(jsonPath("$[1].balance").value(BigDecimal.TEN))
                .andExpect(jsonPath("$[2].cardNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$[2].status").value("EXPIRED"))
                .andExpect(jsonPath("$[2].balance").value(BigDecimal.ONE));
    }

    @Test
    void getAllCardsByUser_WithPage_2_Size_2_ShouldReturnCard() throws Exception {

        List<Card> cards = List.of(card3);
        Page<Card> cardPage = new PageImpl<>(cards);
        PageRequest pageRequest = PageRequest.of(1, 2);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cardRepository.findByOwnerId(anyLong(), eq(pageRequest))).thenReturn(cardPage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user?page=2&size=2")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerName").value("username"))
                .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$[0].status").value("EXPIRED"))
                .andExpect(jsonPath("$[0].balance").value(BigDecimal.ONE));
    }
}
