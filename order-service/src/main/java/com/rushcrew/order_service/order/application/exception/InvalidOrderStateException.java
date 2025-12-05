package com.rushcrew.order_service.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;

public class InvalidOrderStateException extends BusinessException {
	public InvalidOrderStateException() {
		super(OrderErrorCode.INVALID_ORDER_STATE);
	}
	public InvalidOrderStateException(OrderErrorCode orderErrorCode) {
		super(OrderErrorCode.RESERVATION_EXPIRED);
	}
}

