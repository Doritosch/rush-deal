package com.rushcrew.auth_service.auth.application;

import com.rushcrew.auth_service.auth.domain.entity.BlacklistedToken;
import com.rushcrew.auth_service.auth.domain.entity.BlacklistedUser;
import com.rushcrew.auth_service.auth.domain.repository.AccessTokenBlacklistRepository;
import com.rushcrew.auth_service.auth.domain.repository.UserBlacklistRepository;
import com.rushcrew.auth_service.auth.domain.vo.TokenId;
import com.rushcrew.auth_service.auth.domain.vo.UserId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final UserBlacklistRepository userBlacklistRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    /**
     * Access 토큰을 블랙리스트에 추가
     */
    @Transactional
    public void blacklistAccessToken(
        String accessToken,
        LocalDateTime expiresAt
    ) {
        if (expiresAt.isAfter(LocalDateTime.now())) {
            accessTokenBlacklistRepository.addToBlacklist(
                BlacklistedToken.create(TokenId.of(accessToken), expiresAt)
            );
        }
    }

    /**
     * 특정 유저를 블랙리스트에 추가
     */
    @Transactional
    public void blacklistUser(Long userId, LocalDateTime expiresAt) {
        if (expiresAt.isAfter(LocalDateTime.now())) {
            BlacklistedUser blacklistedUser = BlacklistedUser.create(
                UserId.of(userId),
                expiresAt
            );
            userBlacklistRepository.blacklistUser(blacklistedUser);
        }
    }
}
