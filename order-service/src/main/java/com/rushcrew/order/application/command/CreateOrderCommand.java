package com.rushcrew.order.application.command;

import java.math.BigDecimal;
import java.util.List;

import com.rushcrew.order.domain.vo.ShippingInfo;

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

	@Getter
	@Builder
	@AllArgsConstructor
	public static class OrderItemCommand {
		private String timeDealStockId;
		private Integer quantity;
	}
}
