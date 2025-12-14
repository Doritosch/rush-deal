package com.rushcrew.order_service.application.command.port.out;

import java.util.UUID;

import com.rushcrew.order_service.application.query.dto.OrderDetailDto;

public interface OrderCachePort {
	/* 주문 수정 시, 캐시 업데이트 */
	void updateOrderCache(UUID orderId, OrderDetailDto orderDetailDto);

	/* 캐시 존재 여부 확인 */
	boolean existsInCache(UUID orderId);
}
