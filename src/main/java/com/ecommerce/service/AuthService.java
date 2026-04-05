package com.ecommerce.service;

import com.ecommerce.dto.user.AuthResponseDto;
import com.ecommerce.dto.user.LoginRequestDto;
import com.ecommerce.dto.user.UserCreateDto;
import com.ecommerce.entity.user.RefreshToken;
import com.ecommerce.entity.user.User;
import com.ecommerce.exception.AuthenticationException;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.repository.RefreshTokenRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя
     */
    public AuthResponseDto register(UserCreateDto dto) {
        log.info("Registering new user with email: {}", dto.getEmail());

        var userDto = userService.createUser(dto);
        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthenticationException("User registration failed"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, refreshTokenStr);

        log.info("User registered successfully: {}", dto.getEmail());

        return new AuthResponseDto(accessToken, refreshTokenStr, userDto);
    }

    /**
     * Вход (login)
     */
    public AuthResponseDto login(LoginRequestDto dto) {
        log.info("User login attempt with email: {}", dto.getEmail());

        User user = userService.getUserByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", dto.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            log.warn("User is inactive: {}", dto.getEmail());
            throw new AuthenticationException("User account is inactive");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, refreshTokenStr);

        log.info("User logged in successfully: {}", dto.getEmail());

        return new AuthResponseDto(accessToken, refreshTokenStr, userMapper.toDto(user));
    }

    /**
     * Обновить токен доступа
     */
    @Transactional
    public AuthResponseDto refreshAccessToken(String refreshTokenStr) {
        log.debug("Refreshing access token");

        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            throw new AuthenticationException("Refresh token is required");
        }

        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> {
                    log.warn("Attempt to use non-existent refresh token: {}", refreshTokenStr);
                    return new AuthenticationException("Invalid or revoked refresh token");
                });

        if (oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new AuthenticationException("Refresh token has expired");
        }

        User user = oldToken.getUser();
        if (user == null || !user.getIsActive()) {
            throw new AuthenticationException("User is inactive or not found");
        }

        if (!jwtTokenProvider.isRefreshTokenValid(refreshTokenStr)) {
            throw new AuthenticationException("Provided token is not a refresh token");
        }

        refreshTokenRepository.delete(oldToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshTokenStr = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, newRefreshTokenStr);

        log.info("Tokens rotated successfully for user: {}", user.getEmail());

        return new AuthResponseDto(newAccessToken, newRefreshTokenStr, userMapper.toDto(user));
    }

    /**
     * Сохранить refresh token в БД
     */
    private void saveRefreshToken(User user, String tokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    RefreshToken newR = new RefreshToken();
                    newR.setUser(user);
                    return newR;
                });

        refreshToken.setToken(tokenString);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setUpdatedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token updated/saved for user: {}", user.getEmail());
    }
}