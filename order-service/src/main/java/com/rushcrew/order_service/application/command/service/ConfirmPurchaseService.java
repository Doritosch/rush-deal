package com.rushcrew.order_service.application.command.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.application.command.dto.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;
import com.rushcrew.order_service.application.command.usecase.ConfirmPurchaseUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfirmPurchaseService implements ConfirmPurchaseUseCase {
	@Override
	public ConfirmPurchaseResult confirmPurchase(ConfirmPurchaseCommand command) {
		return null;
	}
}
