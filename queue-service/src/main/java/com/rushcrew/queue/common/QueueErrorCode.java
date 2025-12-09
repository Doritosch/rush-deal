package com.rushcrew.queue.common;

import com.rushcrew.common.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum QueueErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND,  "NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "POLICY_NOT_FOUND", "타임딜 정책 정보를 찾을 수 없습니다."),
    POLICY_ALREADY_EXISTS(HttpStatus.CONFLICT,  "POLICY_ALREADY_EXISTS", "해당 상품에 대한 대기열 정책이 이미 존재합니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_ACCESS", "접근 권한이 없습니다."),
    POLICY_ALREADY_DELETED(HttpStatus.NOT_FOUND, "POLICY_ALREADY_DELETED", "이미 삭제된 정책 정보입니다."),
    ROLE_NOT_EXISTS(HttpStatus.NOT_FOUND, "ROLE_NOT_EXISTS", "유효하지 않은 권한입니다."),


    ;

    private final HttpStatus status;
    private final String name;
    private final String message;

    QueueErrorCode(HttpStatus status, String name, String message) {
        this.status = status;
        this.name = name;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
