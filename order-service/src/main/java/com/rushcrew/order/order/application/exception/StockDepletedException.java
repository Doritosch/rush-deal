package com.rushcrew.order.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.order.application.error.OrderErrorCode;

public class StockDepletedException extends BusinessException {
	public StockDepletedException() {
		super(OrderErrorCode.STOCK_DEPLETED);
	}
}
