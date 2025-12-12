package com.rushcrew.order_service.application.command.usecase;

import com.rushcrew.order_service.application.command.dto.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;

public interface ConfirmPurchaseUseCase {
	ConfirmPurchaseResult confirmPurchase(ConfirmPurchaseCommand command);
}
