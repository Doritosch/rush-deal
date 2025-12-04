package com.rushcrew.api_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.api_gateway.config.GatewayProperties;
import com.rushcrew.api_gateway.jwt.JwtDecoder;
import com.rushcrew.api_gateway.jwt.RequestTokenExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;
    private final GatewayProperties gatewayProperties;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        GatewayFilterChain chain
    ) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        Optional<String> accessToken = RequestTokenExtractor.extractAccessToken(
            request
        );

        if (accessToken.isEmpty()) {
            return onError(exchange, "인증 토큰이 필요합니다.");
        }

        String token = accessToken.get();

        try {
            Claims claims = jwtDecoder.validateAndGetClaims(token);

            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            if (userId == null || email == null || role == null) {
                return onError(exchange, "토큰에 필수 정보가 없습니다.");
            }

            // TODO: Redis Blacklist

            ServerHttpRequest mutatedRequest = request
                .mutate()
                .header(USER_ID_HEADER, userId)
                .header(USER_EMAIL_HEADER, URLEncoder.encode(email, StandardCharsets.UTF_8))
                .header(USER_ROLE_HEADER, role)
                .build();

            return chain.filter(
                exchange.mutate().request(mutatedRequest).build()
            );
        } catch (ExpiredJwtException e) {
            return onError(exchange, "만료된 토큰입니다.");
        } catch (MalformedJwtException | SignatureException e) {
            return onError(exchange, "유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            return onError(exchange, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private boolean isPublicPath(String path) {
        return gatewayProperties
            .publicPaths()
            .stream()
            .anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setDetail(message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(pd);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}