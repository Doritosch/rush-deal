package com.rushcrew.order_service.application.command.usecase;

import com.rushcrew.order_service.application.command.dto.command.UpdateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.UpdateOrderResult;

public interface UpdateOrderUseCase {
	UpdateOrderResult updateOrder(UpdateOrderCommand command);
}
