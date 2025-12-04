package com.rushcrew.auth_service.auth.application;

import com.rushcrew.auth_service.auth.application.port.AccessTokenProvider;
import com.rushcrew.auth_service.auth.application.port.RefreshTokenProvider;
import com.rushcrew.auth_service.auth.application.result.TokenPairResult;
import com.rushcrew.auth_service.auth.domain.entity.RefreshToken;
import com.rushcrew.auth_service.auth.domain.policy.ConcurrentLoginPolicy;
import com.rushcrew.auth_service.auth.domain.repository.RefreshTokenRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TokenService {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ConcurrentLoginPolicy concurrentLoginPolicy;
    private final long refreshExpiration;

    public TokenService(
        AccessTokenProvider accessTokenProvider,
        RefreshTokenProvider refreshTokenProvider,
        RefreshTokenRepository refreshTokenRepository,
        ConcurrentLoginPolicy concurrentLoginPolicy,
        @Value("${jwt.refresh.expiration}") long refreshExpiration
    ) {
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.concurrentLoginPolicy = concurrentLoginPolicy;
        this.refreshExpiration = refreshExpiration;
    }

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
        String refreshToken = issueRefreshToken(userId);

        return new TokenPairResult(accessToken, refreshToken);
    }

    @Transactional
    public String issueRefreshToken(Long userId) {
        String tokenValue = refreshTokenProvider.generateToken(userId);

        RefreshToken token = RefreshToken.create(
            tokenValue,
            userId,
            refreshExpiration
        );

        refreshTokenRepository.save(token);

        // 동시 로그인 제한 적용
        List<String> existingTokens = refreshTokenRepository.findAllTokensByUserId(userId);

        List<String> tokensToRevoke = concurrentLoginPolicy.getTokensToRevoke(existingTokens);

        tokensToRevoke.forEach(refreshTokenRepository::deleteByToken);

        return tokenValue;
    }

    public Optional<Long> validateToken(String tokenValue) {
        return refreshTokenRepository
            .findByToken(tokenValue)
            .filter(token -> !token.isExpired())
            .map(RefreshToken::getUserId);
    }

    @Transactional
    public void revokeToken(String tokenValue) {
        refreshTokenRepository.deleteByToken(tokenValue);
    }

    @Transactional
    public void revokeAllTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
