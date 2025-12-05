package com.rushcrew.order_service.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;

public class OrderNotFoundException extends BusinessException {
	public OrderNotFoundException() {
		super(OrderErrorCode.ORDER_NOT_FOUND);
	}
}
