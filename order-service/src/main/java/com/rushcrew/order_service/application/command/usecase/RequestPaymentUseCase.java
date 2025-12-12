package com.rushcrew.order_service.application.command.usecase;

import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;
import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;

public interface RequestPaymentUseCase {
	RequestPaymentResult requestPayment(RequestPaymentCommand command);
}
