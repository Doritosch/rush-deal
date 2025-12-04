package com.rushcrew.order.application.exception;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order.application.error.OrderErrorCode;

public class PurchaseLimitExceededException extends BusinessException {
	public PurchaseLimitExceededException() {
		super(OrderErrorCode.PURCHASE_LIMIT_EXCEEDED);
	}
}
