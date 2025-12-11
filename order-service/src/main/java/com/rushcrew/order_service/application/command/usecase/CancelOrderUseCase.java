package com.rushcrew.order_service.application.command.usecase;

import com.rushcrew.order_service.application.command.dto.result.CancelOrderResult;
import com.rushcrew.order_service.application.command.dto.command.CancelOrderCommand;

public interface CancelOrderUseCase {
	CancelOrderResult cancelOrder(CancelOrderCommand command);
}
