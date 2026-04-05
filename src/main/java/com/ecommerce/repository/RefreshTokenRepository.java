package com.ecommerce.repository;

import com.ecommerce.entity.user.RefreshToken;
import com.ecommerce.entity.user.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends BaseRepository<RefreshToken> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    boolean existsByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime expiryDate);

    void deleteByUserId(UUID userId);
}

