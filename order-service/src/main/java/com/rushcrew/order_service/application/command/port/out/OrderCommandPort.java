package com.rushcrew.order_service.application.command.port.out;

import java.util.UUID;

public interface OrderCommandPort {
	/* 기존 구매 수량 */
	Integer getTotalPurchasedQuantity(Long userId, UUID timeDealId);
}
