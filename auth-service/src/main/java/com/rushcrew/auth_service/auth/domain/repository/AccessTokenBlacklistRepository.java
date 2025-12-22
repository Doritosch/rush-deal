package com.rushcrew.auth_service.auth.domain.repository;

import com.rushcrew.auth_service.auth.domain.entity.BlacklistedToken;

public interface AccessTokenBlacklistRepository {
    void addToBlacklist(BlacklistedToken token);
}