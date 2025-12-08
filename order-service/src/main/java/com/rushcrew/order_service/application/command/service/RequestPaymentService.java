package com.rushcrew.order_service.application.command.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;
import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;
import com.rushcrew.order_service.application.command.usecase.RequestPaymentUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestPaymentService implements RequestPaymentUseCase {
	@Override
	public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
		return null;
	}
}
