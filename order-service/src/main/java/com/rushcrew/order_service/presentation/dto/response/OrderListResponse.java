package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.application.query.dto.OrderListDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderListResponse {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Integer itemCount;
	private String firstProductName; // 첫 번째 상품명

	public static OrderListResponse from(OrderListDto dto) {
		return OrderListResponse.builder()
			.orderId(dto.getOrderId())
			.orderStatus(dto.getOrderStatus())
			.finalAmount(dto.getFinalAmount())
			.orderedAt(dto.getOrderedAt())
			.itemCount(dto.getItemCount())
			.firstProductName(dto.getFirstProductName())
			.build();
	}
}
