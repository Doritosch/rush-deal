package com.rushcrew.order_service.application.command.port.out;

import java.util.UUID;

import com.rushcrew.order_service.application.command.dto.result.CreatedOrderInfo;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;

public interface OrderCachePort {
	/* 주문 생성 시, 주문 캐시 저장 */
	void saveOrderCache(CreatedOrderInfo info);

	/* 주문 수정 시, 캐시 업데이트 */
	void updateOrderCache(UUID orderId, OrderDetailDto orderDetailDto);

	boolean existsInCache(UUID orderId);
}
