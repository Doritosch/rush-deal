package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class DuplicateOrderItemException extends BusinessException {
	public DuplicateOrderItemException() {
		super(OrderErrorCode.DUPLICATE_ORDER_ITEM);
	}
}
