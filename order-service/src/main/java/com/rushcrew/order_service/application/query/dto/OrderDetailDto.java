package com.rushcrew.order_service.application.query.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.domain.vo.ShippingInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
	private UUID orderId;
	private Long userId;
	private String orderStatus;
	private BigDecimal totalAmount;
	private Long pointUsed;
	private BigDecimal finalAmount;
	private Instant orderedAt;
	private Instant paymentCompletedAt;
	private Instant purchaseConfirmedAt;
	private Instant cancelledAt;
	private Instant autoConfirmScheduledAt;
	private ShippingInfo shippingInfo;
	private List<OrderItemQueryDto> orderItems;

	public static OrderDetailDto fromEntity(Order order) {
		return OrderDetailDto.builder()
			.orderId(order.getOrderId())
			.userId(order.getUserId())
			.orderStatus(order.getStatus().name())
			.totalAmount(order.getTotalAmount())
			.pointUsed(order.getPointUsed())
			.finalAmount(order.getFinalAmount())
			.orderedAt(order.getOrderedAt())
			.paymentCompletedAt(order.getPaymentCompletedAt())
			.purchaseConfirmedAt(order.getPurchaseConfirmedAt())
			.cancelledAt(order.getCancelledAt())
			.autoConfirmScheduledAt(order.getAutoConfirmScheduledAt())
			.shippingInfo(order.getShippingInfo())
			.orderItems(order.getOrderItems().stream()
				.map(item -> OrderItemQueryDto.builder()
					.orderItemId(item.getOrderItemId())
					.productName(item.getProductName())
					.optionName(item.getProductSnapshot().optionName())
					.quantity(item.getQuantity())
					.unitPrice(item.getUnitPrice())
					.discountPrice(item.getDiscountPrice())
					.subtotal(item.getSubtotal())
					.build())
				.collect(Collectors.toList()))
			.build();
	}
}
