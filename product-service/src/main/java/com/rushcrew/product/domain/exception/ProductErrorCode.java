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

    // 상품
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "P-001", "존재하지 않는 상품입니다."),
    REQUIRED_SELLER_ID(HttpStatus.BAD_REQUEST, "P-002", "MASTER로 상품 생성 시 sellerId는 필수입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String name;
    private final String message;
}
