package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class InvalidQueueTokenException extends BusinessException {
	public InvalidQueueTokenException() {
		super(OrderErrorCode.INVALID_QUEUE_TOKEN);
	}
}
