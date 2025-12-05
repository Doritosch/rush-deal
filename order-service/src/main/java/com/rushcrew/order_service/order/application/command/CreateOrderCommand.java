package com.rushcrew.order_service.order.application.command;

import java.math.BigDecimal;
import java.util.List;

import com.rushcrew.order_service.order.domain.vo.ShippingInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {

	private Long userId;
	private String timeDealId;
	private List<OrderItemCommand> orderItems;
	private BigDecimal pointUsed;
	private ShippingInfo shippingInfo;
	private String paymentMethod;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class OrderItemCommand {
		private String timeDealStockId;
		private Integer quantity;
	}
}
