package com.rushcrew.payment_service.domain.exception;

import com.rushcrew.common.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INVALID_PAYMENT(HttpStatus.FORBIDDEN, "INVALID_PAYMENT", "존재하지 않는 결제 내역입니다."),
    FAILED_VERIFYING_PAYMENT(HttpStatus.BAD_REQUEST, "FAILED_VERIFYING_PAYMENT", "결제 내역 검증에 실패했습니다."),
    NOT_COMPLETED_PAYMENT(HttpStatus.BAD_REQUEST, "NOT_COMPLETED_PAYMENT", "결제가 완료되지 않았습니다."),
    NOT_FOUND_PORTONE_ID(HttpStatus.NOT_FOUND, "NOT_FOUND_PORTONE_ID", "portone id를 찾을 수 없습니다."),
    NOT_FOUND_PAYMENT_TRANSACTION(HttpStatus.NOT_FOUND, "NOT_FOUND_PAYMENT_TRANSACTION", "결제 트랜잭션을 찾을 수 없습니다."),
    FAILED_CANCEL_PAYMENT(HttpStatus.BAD_REQUEST, "FAILED_CANCEL_PAYMENT", "결제 취소에 실패했습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문 정보를 찾을 수 없습니다."),
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "AMOUNT_MISMATCH", "결제 금액이 주문 금액과 일치하지 않습니다."),
    INVALID_WEBHOOK(HttpStatus.BAD_REQUEST, "INVALID_WEBHOOK", "유효하지 않은 웹훅입니다."),
    EVENT_SERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "EVENT_SERIALIZATION_FAILED", "이벤트 직렬화에 실패했습니다."),
    ALREADY_PUBLISHED_STATUS(HttpStatus.BAD_REQUEST, "ALREADY_PUBLISHED_STATUS", "이미 PUBLISHED 상태인 이벤트 입니다."),
    ALREADY_FAILED_STATUS(HttpStatus.BAD_REQUEST, "ALREADY_FAILED_STATUS", "이미 FAILED 상태인 이벤트 입니다."),
    CONCURRENT_UPDATE_DETECTED(HttpStatus.CONFLICT, "CONCURRENT_UPDATE_DETECTED", "동시에 결제 정보가 수정되었습니다. 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String name;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
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
