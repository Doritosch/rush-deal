package com.rushcrew.order_service.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;

public class UnauthorizedException extends BusinessException {
	public UnauthorizedException() {
		super(OrderErrorCode.UNAUTHORIZED);
	}
}
