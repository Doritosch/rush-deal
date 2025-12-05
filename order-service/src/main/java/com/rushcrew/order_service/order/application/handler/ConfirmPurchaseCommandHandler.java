package com.rushcrew.order_service.order.application.handler;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.order.application.command.ConfirmPurchaseResult;
import com.rushcrew.order_service.order.application.service.PurchaseConfirmationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfirmPurchaseCommandHandler {
	private final PurchaseConfirmationService purchaseConfirmationService;

	@Transactional
	public ConfirmPurchaseResult handle(ConfirmPurchaseCommand command) {
		return purchaseConfirmationService.confirmPurchase(command);
	}
}
