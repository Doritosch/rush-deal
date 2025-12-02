package com.rushcrew.api_gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtDecoder {

    private final JwtProperties jwtProperties;

    private SecretKey accessSecretkey;

    @PostConstruct
    public void init() {
        this.accessSecretkey = Keys.hmacShaKeyFor(jwtProperties.access().secret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateAndGetClaims(String token) {
        return
            Jwts.parser()
                .verifyWith(accessSecretkey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
