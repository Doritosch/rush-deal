package com.rushcrew.order_service.order.application.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.order_service.order.application.command.RequestPaymentCommand;
import com.rushcrew.order_service.order.application.command.RequestPaymentResult;
import com.rushcrew.order_service.order.application.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestPaymentCommandHandler {

	private final PaymentService paymentService;

	@Transactional
	public RequestPaymentResult handle(RequestPaymentCommand command) {
		return paymentService.requestPayment(command);
	}
}
