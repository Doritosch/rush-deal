package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class StockDepletedException extends BusinessException {
	public StockDepletedException() {
		super(OrderErrorCode.STOCK_DEPLETED);
	}
}
