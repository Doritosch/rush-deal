package com.rushcrew.timedeal.common.exception;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.common.dto.ErrorResponse;
import com.rushcrew.common.global.error.CommonErrorCode;
import com.rushcrew.common.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Common 핸들러보다 먼저 실행되도록 우선순위 높임
public class SecurityExceptionHandler {

    /**
     * Spring Security 6 이상에서 발생하는 인가(Authorization) 거부 예외 처리 (@PreAuthorize 실패 시 이 예외가 발생함)
     */
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception e) {
        log.error("[Security Error] Access Denied: {}", e.getMessage());

        // Common에 정의된 403 ErrorCode 사용 (혹은 직접 생성)
        ErrorCode errorCode = CommonErrorCode.FORBIDDEN;

        // ErrorResponse 생성 (Common의 스펙에 맞춤)
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(errorResponse));
    }
}
