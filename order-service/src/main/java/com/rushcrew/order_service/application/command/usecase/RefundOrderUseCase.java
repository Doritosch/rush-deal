package com.rushcrew.order_service.application.command.usecase;

import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;

public interface RefundOrderUseCase {
	RefundOrderResult refundOrder(RefundOrderCommand command);
}
