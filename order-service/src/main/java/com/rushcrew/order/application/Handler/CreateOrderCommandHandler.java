package com.rushcrew.order.application.handler;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.command.CreateOrderCommand;
import com.rushcrew.order.application.command.CreateOrderResult;
import com.rushcrew.order.application.service.OrderCreationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler {
	private final OrderCreationService orderCreationService;

	public CreateOrderResult handle(CreateOrderCommand command) {
		return orderCreationService.createOrder(command);
	}
}
