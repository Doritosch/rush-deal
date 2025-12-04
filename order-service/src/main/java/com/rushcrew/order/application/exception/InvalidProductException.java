package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class InvalidProductException extends BusinessException {
	public InvalidProductException() {
		super(OrderErrorCode.INVALID_PRODUCT);
	}
}
