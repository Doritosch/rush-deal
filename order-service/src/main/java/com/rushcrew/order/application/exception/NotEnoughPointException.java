package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class NotEnoughPointException extends BusinessException {
	public NotEnoughPointException() {
		super(OrderErrorCode.NOT_ENOUGH_POINTS);
	}
}
