package com.rushcrew.auth_service.auth.infrastructure.jwt;

import com.rushcrew.auth_service.auth.application.port.AccessTokenProvider;
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
public class JwtAccessTokenProvider implements AccessTokenProvider {

    private final JwtProperties properties;

    private SecretKey accessSecretKey;

    @PostConstruct
    public void init() {
        this.accessSecretKey = Keys.hmacShaKeyFor(
            properties.access().secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(Long userId, String email, String role) {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .claim("tokenType", "access")
            .id(UUID.randomUUID().toString())
            .issuer("rush-deal")
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + properties.access().expiration())
            )
            .signWith(accessSecretKey)
            .compact();
    }
}
