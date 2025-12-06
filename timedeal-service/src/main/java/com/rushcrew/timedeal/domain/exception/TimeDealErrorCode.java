package com.rushcrew.timedeal.domain.exception;

import com.rushcrew.common.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TimeDealErrorCode implements ErrorCode {

    REQUIRED_TITLE(HttpStatus.BAD_REQUEST, "TD-001", "타임딜 제목 입력은 필수입니다."),
    REQUIRED_DESCRIPTION(HttpStatus.BAD_REQUEST, "TD-002", "타임딜 설명 입력은 필수입니다."),
    INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST, "TD-003", "가격은 0원 미만일 수 없습니다."),
    INVALID_LIMIT_QUANTITY(HttpStatus.BAD_REQUEST, "TD-004", "구매 제한 수량은 NULL 혹은 1이상이어야 합니다."),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "TD-005", "타임딜 기간 설정이 올바르지 않습니다."),
    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "TD-006", "존재하지 않는 주문입니다."),
    INVALID_QUANTITY_RANGE(HttpStatus.BAD_REQUEST, "TD-007", "수량은 반드시 1이상의 양수여야 합니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "TD-008", "존재하지 않는 상품입니다."),
    NOT_FOUND_OPTION(HttpStatus.NOT_FOUND, "TD-009", "존재하지 않는 상품옵션입니다."),
    INVALID_STOCK_COUNT(HttpStatus.BAD_REQUEST, "TD-010", "재고 수량은 0개 미만일 수 없습니다."),
    REQUIRED_STOCK_COUNT(HttpStatus.BAD_REQUEST, "TD-011", "재고 수량은 NULL일 수 없습니다."),
    MUST_BE_CHEAPER(HttpStatus.BAD_REQUEST, "TD-012", "타임딜 할인가격은 기존 가격보다 낮아야 합니다."),

    ;

    private final HttpStatus httpStatus;
    private final String name;
    private final String message;
}
