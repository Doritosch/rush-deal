package com.rushcrew.order_service.order.application.handler;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.command.CreateOrderCommand;
import com.rushcrew.order_service.order.application.command.CreateOrderResult;
import com.rushcrew.order_service.order.application.service.OrderCreationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler {
	private final OrderCreationService orderCreationService;

	public CreateOrderResult handle(CreateOrderCommand command) {
		return orderCreationService.createOrder(command);
	}
}
