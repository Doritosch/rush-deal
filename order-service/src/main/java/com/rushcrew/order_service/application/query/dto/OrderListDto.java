package com.rushcrew.order_service.application.query.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.domain.model.order.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDto {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Integer itemCount;
	private String firstProductName;

	public static OrderListDto fromEntity(Order order) {
		String firstProductName = order.getOrderItems().isEmpty()
			? ""
			: order.getOrderItems().get(0).getProductName();

		return OrderListDto.builder()
			.orderId(order.getOrderId())
			.orderStatus(order.getStatus().name())
			.finalAmount(order.getFinalAmount())
			.orderedAt(order.getOrderedAt())
			.itemCount(order.getOrderItems().size())
			.firstProductName(firstProductName)
			.build();
	}
}
