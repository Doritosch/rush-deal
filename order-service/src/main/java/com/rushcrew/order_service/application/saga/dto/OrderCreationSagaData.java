package com.rushcrew.order_service.application.saga.dto;

import java.util.List;
import java.util.UUID;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.domain.vo.ProductSnapshot;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationSagaData {

	/**
	 * 주문 요청 원본
	 */
	private CreateOrderCommand command;

	/**
	 * ValidateStockStep에서 확정된 상품 스냅샷
	 * (타임딜, 옵션, 가격, 할인 정보 포함)
	 */
	private List<ProductSnapshot> productSnapshots;

	/**
	 * 주문 생성 결과
	 */
	private UUID orderId;

	public void bindOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public boolean isOrderCreated() {
		return orderId != null;
	}
}
