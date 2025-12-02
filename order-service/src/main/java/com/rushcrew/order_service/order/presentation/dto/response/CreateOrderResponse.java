package com.rushcrew.order_service.order.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rushcrew.order_service.order.application.command.CreateOrderResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderResponse {

	private UUID orderId;
	private String orderStatus;
	private BigDecimal totalAmount;
	private BigDecimal pointUsed;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Instant reservationExpiresAt;
	private String message;
	private List<OrderItemResponse> orderItems;

	@Getter
	@Builder
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

	public static CreateOrderResponse from(CreateOrderResult result) {
		List<OrderItemResponse> items = result.getOrderItems().stream()
			.map(item -> OrderItemResponse.builder()
				.orderItemId(item.getOrderItemId())
				.productName(item.getProductName())
				.optionName(item.getOptionName())
				.quantity(item.getQuantity())
				.unitPrice(item.getUnitPrice())
				.discountPrice(item.getDiscountPrice())
				.subtotal(item.getSubtotal())
				.build())
			.collect(Collectors.toList());

		return CreateOrderResponse.builder()
			.orderId(result.getOrderId())
			.orderStatus(result.getOrderStatus())
			.totalAmount(result.getTotalAmount())
			.pointUsed(result.getPointUsed())
			.finalAmount(result.getFinalAmount())
			.orderedAt(result.getOrderedAt())
			.reservationExpiresAt(result.getReservationExpiresAt())
			.message("주문이 성공적으로 생성되었습니다. 15분 내에 결제를 완료해주세요.")
			.orderItems(items)
			.build();
	}
}
