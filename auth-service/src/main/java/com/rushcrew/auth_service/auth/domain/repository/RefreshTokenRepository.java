package com.rushcrew.auth_service.auth.domain.repository;

import com.rushcrew.auth_service.auth.domain.entity.RefreshToken;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String tokenValue);
    List<String> findAllTokensByUserId(Long userId);
    void deleteByToken(String tokenValue);
    void deleteAllByUserId(Long userId);
}
