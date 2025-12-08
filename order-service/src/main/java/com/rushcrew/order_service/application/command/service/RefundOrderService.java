package com.rushcrew.order_service.application.command.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;
import com.rushcrew.order_service.application.command.usecase.RefundOrderUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundOrderService implements RefundOrderUseCase {
	@Override
	public RefundOrderResult refundOrder(RefundOrderCommand command) {
		return null;
	}
}
