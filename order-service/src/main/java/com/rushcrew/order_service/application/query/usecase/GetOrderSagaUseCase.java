package com.rushcrew.order_service.application.query.usecase;

import java.util.UUID;

import com.rushcrew.order_service.application.query.dto.OrderSagaResult;

public interface GetOrderSagaUseCase {
	OrderSagaResult getSaga(UUID sagaId);
}
