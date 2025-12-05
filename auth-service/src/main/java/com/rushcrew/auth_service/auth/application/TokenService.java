package com.rushcrew.auth_service.auth.application;

import com.rushcrew.auth_service.auth.application.policy.TokenPolicy;
import com.rushcrew.auth_service.auth.application.port.AccessTokenProvider;
import com.rushcrew.auth_service.auth.application.port.RefreshTokenProvider;
import com.rushcrew.auth_service.auth.application.result.TokenPairResult;
import com.rushcrew.auth_service.auth.application.result.UserInfoResult;
import com.rushcrew.auth_service.auth.domain.entity.RefreshToken;
import com.rushcrew.auth_service.auth.domain.policy.ConcurrentLoginPolicy;
import com.rushcrew.auth_service.auth.domain.repository.RefreshTokenRepository;
import com.rushcrew.auth_service.auth.domain.vo.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final TokenPolicy tokenPolicy;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistService tokenBlacklistService;
    private final ConcurrentLoginPolicy concurrentLoginPolicy;

    @Transactional
    public TokenPairResult issueTokenPair(
        Long userId,
        String email,
        String role
    ) {
        String accessToken = accessTokenProvider.generateToken(
            userId,
            email,
            role
        );
        String refreshToken = refreshTokenProvider.generateToken(userId);

        RefreshToken token = RefreshToken.create(
            refreshToken,
            UserId.of(userId),
            tokenPolicy.refreshExpirationMillis()
        );

        refreshTokenRepository.save(token);

        // 동시 로그인 정책 적용
        List<String> existingTokens =
            refreshTokenRepository.findAllTokensByUserId(userId);
        List<String> tokensToRevoke = concurrentLoginPolicy.getTokensToRevoke(
            existingTokens
        );
        tokensToRevoke.forEach(refreshTokenRepository::deleteByToken);

        return new TokenPairResult(accessToken, refreshToken);
    }

    @Transactional
    public String refreshAccessToken(
        UserInfoResult user,
        String refreshTokenValue
    ) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다.")
            );

        token.ensureValid();

        return accessTokenProvider.generateToken(
            user.id(),
            user.name(),
            user.role()
        );
    }

    @Transactional
    public void revokeToken(String accessToken, String refreshToken) {
        // 1. Refresh Token 삭제
        Optional.ofNullable(refreshToken).ifPresent(
            refreshTokenRepository::deleteByToken
        );

        // 2. Access Token 블랙리스트 처리
        java.time.LocalDateTime expiryDate = accessTokenProvider.getExpiryDate(
            accessToken
        );
        tokenBlacklistService.blacklistAccessToken(accessToken, expiryDate);
    }

    /**
     * 모든 토큰 폐기 (전체 로그아웃)
     */
    @Transactional
    public void revokeAllTokens(Long userId, String currentAccessToken) {
        // 1. 해당 유저의 모든 Refresh Token 삭제
        refreshTokenRepository.deleteAllByUserId(userId);

        // 2. 현재 Access Token 및 유저 자체를 블랙리스트 처리
        java.time.LocalDateTime expiryDate = accessTokenProvider.getExpiryDate(
            currentAccessToken
        );
        tokenBlacklistService.blacklistAccessToken(
            currentAccessToken,
            expiryDate
        );
        tokenBlacklistService.blacklistUser(userId, expiryDate);
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        return refreshTokenProvider.getUserIdFromToken(refreshToken);
    }
}
