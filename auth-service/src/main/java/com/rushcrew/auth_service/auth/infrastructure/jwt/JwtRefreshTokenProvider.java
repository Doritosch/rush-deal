package com.rushcrew.auth_service.auth.infrastructure.jwt;

import com.rushcrew.auth_service.auth.application.port.RefreshTokenProvider;
import com.rushcrew.auth_service.auth.infrastructure.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtRefreshTokenProvider implements RefreshTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey refreshSecretKey;

    @PostConstruct
    public void init() {
        this.refreshSecretKey = Keys.hmacShaKeyFor(
            jwtProperties.refresh().secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.refresh().expiration());

        return Jwts.builder()
            .subject(userId.toString())
            .claim("tokenType", "refresh")
            .id(UUID.randomUUID().toString())
            .issuer("rush-deal")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(refreshSecretKey)
            .compact();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(
            Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }
}
