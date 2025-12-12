package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {

	private UUID orderId;
	private String orderStatus;
	private BigDecimal totalAmount;
	private Long pointUsed;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Instant reservationExpiresAt;
	private String message;
	private List<OrderItemResponse> orderItems;

	@Getter
	@AllArgsConstructor
	public static class OrderItemResponse {
		private UUID orderItemId;
		private String productName;
		private String optionName;
		private Integer quantity;
		private BigDecimal unitPrice;
		private BigDecimal discountPrice;
		private BigDecimal subtotal;
	}
}
