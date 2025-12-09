package com.rushcrew.product.domain.exception;

import com.rushcrew.common.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    OPTION_LIST_EMPTY(HttpStatus.BAD_REQUEST, "PO-001", "최소 1개 이상의 옵션이 필요합니다."),
    NOT_FOUND_OPTION(HttpStatus.NOT_FOUND, "PO-002", "존재하지 않는 옵션입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String name;
    private final String message;
}
