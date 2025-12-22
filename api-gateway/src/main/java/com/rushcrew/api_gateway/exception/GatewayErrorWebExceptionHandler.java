package com.rushcrew.api_gateway.exception;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.common.dto.ErrorResponse;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.common.global.error.ErrorCode;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class GatewayErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GatewayErrorWebExceptionHandler(
        ErrorAttributes errorAttributes,
        WebProperties.Resources resources,
        ApplicationContext applicationContext,
        ServerCodecConfigurer serverCodecConfigurer
    ) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(serverCodecConfigurer.getWriters());
        setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
        ErrorAttributes errorAttributes
    ) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        if (error instanceof BusinessException businessException) {
            ErrorCode errorCode = businessException.getErrorCode();
            HttpStatus status = errorCode.getHttpStatus();

            ErrorResponse errorResponse = ErrorResponse.of(errorCode);
            ApiResponse<ErrorResponse> response = ApiResponse.error(errorResponse);
            return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(response));
        } else {
            ErrorResponse errorResponse = ErrorResponse.of(GatewayErrorCode.INTERNAL_SERVER_ERROR);
            ApiResponse<ErrorResponse> response = ApiResponse.error(
                errorResponse
            );
            return ServerResponse.status(GatewayErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(response));
        }
    }
}
