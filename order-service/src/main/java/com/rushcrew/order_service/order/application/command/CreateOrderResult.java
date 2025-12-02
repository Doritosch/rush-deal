package com.rushcrew.order_service.order.application.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderResult {

	private UUID orderId;
	private String orderStatus;
	private BigDecimal totalAmount;
	private BigDecimal pointUsed;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Instant reservationExpiresAt; // 예약 만료 시간: 15분 후
	private List<OrderItemResult> orderItems;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class OrderItemResult {
		private UUID orderItemId;
		private String productName;
		private String optionName;
		private Integer quantity;
		private BigDecimal unitPrice;
		private BigDecimal discountPrice;
		private BigDecimal subtotal;
	}
}
