package com.example.bankcards.security;

import com.example.bankcards.entity.SecurityUser;
import com.example.bankcards.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Обрабатывает внутреннюю логику фильтра JWT аутентификации.
     * <p>
     * Метод извлекает JWT токен из заголовка Authorization, проверяет его валидность
     * и аутентифицирует пользователя в контексте безопасности Spring Security при успешной проверке.
     * Если токен отсутствует или невалиден, запрос передается дальше по цепочке фильтров без аутентификации.
     * </p>
     *
     * @param request     HTTP запрос, из которого извлекается JWT токен. Не может быть null
     * @param response    HTTP ответ, который может быть модифицирован при необходимости. Не может быть null
     * @param filterChain цепочка фильтров для передачи управления следующему фильтру в цепочке. Не может быть null
     * @throws ServletException если происходит ошибка при обработке запроса или ответа
     * @throws IOException      если происходит ошибка ввода/вывода при обработке запроса или ответа
     * @see JwtService#extractUserName(String)
     * @see JwtService#isTokenValid(String, UserDetails)
     * @see SecurityContextHolder
     * @see UsernamePasswordAuthenticationToken
     */
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var username = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new SecurityUser(userService.getUserByUsername(username));

            // Если токен валиден, то аутентифицируем пользователя
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
