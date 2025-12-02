package com.rushcrew.order_service.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;

public class InvalidProductException extends BusinessException {
	public InvalidProductException() {
		super(OrderErrorCode.INVALID_PRODUCT);
	}
}
