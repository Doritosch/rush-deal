package com.rushcrew.auth_service.auth.domain.repository;

import com.rushcrew.auth_service.auth.domain.entity.BlacklistedUser;

public interface UserBlacklistRepository {
    /**
     * 사용자를 블랙리스트에 추가 (모든 AccessToken 무효화)
     */
    void blacklistUser(BlacklistedUser user);
}
