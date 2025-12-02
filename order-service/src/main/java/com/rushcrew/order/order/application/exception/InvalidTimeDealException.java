package com.rushcrew.order.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.order.application.error.OrderErrorCode;

public class InvalidTimeDealException extends BusinessException {
	public InvalidTimeDealException() {
		super(OrderErrorCode.INVALID_TIME_DEAL);
	}
}
