package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class InvalidTimeDealException extends BusinessException {
	public InvalidTimeDealException() {
		super(OrderErrorCode.INVALID_TIME_DEAL);
	}
}
