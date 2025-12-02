package com.rushcrew.order.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.order.application.error.OrderErrorCode;

public class DuplicateOrderItemException extends BusinessException {
	public DuplicateOrderItemException() {
		super(OrderErrorCode.DUPLICATE_ORDER_ITEM);
	}
}
