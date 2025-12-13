package com.rushcrew.api_gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.api_gateway.blacklist.BlacklistService;
import com.rushcrew.api_gateway.config.GatewayProperties;
import com.rushcrew.api_gateway.jwt.JwtDecoder;
import com.rushcrew.api_gateway.jwt.RequestTokenExtractor;
import com.rushcrew.api_gateway.model.UserInfo;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtDecoder jwtDecoder;
    private final BlacklistService blacklistService;
    private final GatewayProperties gatewayProperties;
    private final ObjectMapper objectMapper;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        Optional<String> extracted = RequestTokenExtractor.extractAccessToken(exchange.getRequest());

        if (extracted.isEmpty()) {
            return onError(exchange, "인증 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = extracted.get();

        return validateToken(exchange, chain, accessToken);
    }

    // TOKEN 검증
    private Mono<Void> validateToken(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken) {

        return Mono.fromCallable(() -> jwtDecoder.validateAndGetClaims(accessToken))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(ExpiredJwtException.class,
                e -> onError(exchange, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED)
                    .then(Mono.empty()))
            .onErrorResume(SignatureException.class,
                e -> onError(exchange, "서명 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED)
                    .then(Mono.empty()))
            .onErrorResume(MalformedJwtException.class,
                e -> onError(exchange, "유효하지 않은 토큰 형식입니다.", HttpStatus.UNAUTHORIZED)
                    .then(Mono.empty()))
            .flatMap(claims -> processClaims(exchange, chain, accessToken, claims));
    }

    // BLACKLIST 체크
    private Mono<Void> processClaims(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken, Claims claims) {

        return validateClaims(exchange, claims)
            .flatMap(userInfo -> validateBlacklist(exchange, accessToken, userInfo))
            .flatMap(userInfo -> forwardRequestWithHeaders(exchange, chain, userInfo));
    }


    // Claim 필수값 검증
    private Mono<UserInfo> validateClaims(ServerWebExchange exchange, Claims claims) {
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        if (userId == null || email == null || role == null) {
            return onError(exchange, "토큰 정보가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED)
                .then(Mono.empty());
        }

        return Mono.just(new UserInfo(userId, email, role));
    }


    // 블랙리스트 검증
    private Mono<UserInfo> validateBlacklist(ServerWebExchange exchange, String accessToken, UserInfo info) {
        return blacklistService.isTokenBlacklisted(accessToken)
            .flatMap(isTokenBlacklisted -> {
                if (Boolean.TRUE.equals(isTokenBlacklisted)) {
                    return onError(exchange, "로그아웃된 토큰입니다.", HttpStatus.FORBIDDEN)
                        .then(Mono.empty());
                }
                return blacklistService.isUserBlacklisted(Long.valueOf(info.userId()));
            })
            .flatMap(isUserBlacklisted -> {
                if (Boolean.TRUE.equals(isUserBlacklisted)) {
                    return onError(exchange, "모든 기기에서 로그아웃된 사용자입니다.", HttpStatus.FORBIDDEN)
                        .then(Mono.empty());
                }
                return Mono.just(info);
            });
    }


    // Header 주입 후 요청 전달
    private Mono<Void> forwardRequestWithHeaders(ServerWebExchange exchange, GatewayFilterChain chain, UserInfo userInfo) {

        ServerHttpRequest authorizedRequest = exchange.getRequest()
            .mutate()
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_EMAIL_HEADER, URLEncoder.encode(userInfo.email(), StandardCharsets.UTF_8))
            .header(USER_ROLE_HEADER, userInfo.role())
            .build();

        return chain.filter(exchange.mutate().request(authorizedRequest).build());
    }


    private boolean isPublic(String path) {
        return gatewayProperties.publicPaths().stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ProblemDetail body = ProblemDetail.forStatus(status);
        body.setDetail(message);

        try {
            byte[] json = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(json)));
        } catch (Exception ex) {
            byte[] fallback = "{\"error\":\"Internal Server Error\"}".getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(fallback)));
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
