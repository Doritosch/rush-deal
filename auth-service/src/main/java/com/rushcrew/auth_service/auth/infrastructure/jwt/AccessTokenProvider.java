package com.rushcrew.auth_service.auth.infrastructure.jwt;

import com.rushcrew.auth_service.auth.application.port.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider implements TokenProvider {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    private SecretKey accessSecretKey;

    @PostConstruct
    public void init() {
        this.accessSecretKey = Keys.hmacShaKeyFor(
            accessSecret.getBytes(StandardCharsets.UTF_8)
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
            .expiration(new Date(System.currentTimeMillis() + accessExpiration))
            .signWith(accessSecretKey)
            .compact();
    }
}
