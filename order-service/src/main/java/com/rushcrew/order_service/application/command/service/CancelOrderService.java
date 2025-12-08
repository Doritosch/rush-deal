package com.rushcrew.order_service.application.command.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.application.command.dto.command.CancelOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CancelOrderResult;
import com.rushcrew.order_service.application.command.usecase.CancelOrderUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CancelOrderService implements CancelOrderUseCase {
	@Override
	public CancelOrderResult cancelOrder(CancelOrderCommand command) {
		return null;
	}
}
