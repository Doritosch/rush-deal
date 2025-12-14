package com.rushcrew.order_service.domain.enums;

public enum SagaStepName {
	VALIDATE_STOCK,
	REQUEST_STOCK_RESERVATION,
	DEDUCT_POINT,
	CREATE_ORDER
}
