package com.ecommerce.security;

import com.ecommerce.entity.user.User;
import com.ecommerce.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.audience}")
    private String audience;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    /**
     * Сгенерировать access token
     */
    public String generateAccessToken(User user) {
        return generateToken(user, jwtExpirationMs, "access");
    }

    /**
     * Сгенерировать refresh token
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpirationMs, "refresh");
    }

    /**
     * Генерация токена
     */
    private String generateToken(User user, long expirationMs, String tokenType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMs, ChronoUnit.MILLIS);
        String email = user.getEmail();
        String role =  user.getRole().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(email)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("type", tokenType)
                .build();

        if ("access".equals(tokenType)) {
            claims = JwtClaimsSet.from(claims).claim("roles", role).build();
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    /**
     * Извлечь email из токена
     */
    public String extractEmailFromToken(String token) {
        log.debug("Attempting to extract email from token");
        try {
            var jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract email from JWT: {}", e.getMessage());
            throw new AuthenticationException("Invalid token");
        }
    }

    /**
     * Проверить валидность refresh token
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            String tokenType = (String) jwt.getClaims().get("type");
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            log.warn("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Проверить валидность access token
     */
    public boolean isAccessTokenValid(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            String tokenType = (String) jwt.getClaims().get("type");
            return "access".equals(tokenType);
        } catch (Exception e) {
            log.warn("Access token validation failed: {}", e.getMessage());
            return false;
        }
    }
}