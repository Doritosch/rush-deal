package com.rushcrew.api_gateway.exception;

import com.rushcrew.common.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

    // Gateway 관련 에러
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "GATEWAY-001", "게이트웨이 타임아웃이 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GATEWAY-002", "서비스를 사용할 수 없습니다."),

    // 인증 관련 에러
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-001", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-002", "유효하지 않은 토큰입니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰 서명이 유효하지 않습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH-004", "토큰이 없습니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "AUTH-005", "사용할 수 없는 토큰입니다."),

    // 일반 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-001", "서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-002", "잘못된 요청입니다.");



    private final HttpStatus httpStatus;
    private final String name;
    private final String message;
}
