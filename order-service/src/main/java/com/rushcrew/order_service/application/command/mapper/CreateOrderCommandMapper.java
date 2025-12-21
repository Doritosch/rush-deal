package com.rushcrew.order_service.application.command.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.domain.vo.ShippingInfo;
import com.rushcrew.order_service.presentation.dto.request.CreateOrderRequest;

@Component
public class CreateOrderCommandMapper {

	public CreateOrderCommand toCommand(
		CreateOrderRequest request,
		Long userId,
		String role,
		String queueToken
	) {
		return CreateOrderCommand.builder()
			.userId(userId)
			.timeDealId(UUID.fromString(request.timeDealId()))
			.productId(UUID.fromString(request.productId()))
			.queueToken(queueToken)
			.role(role)
			.orderItems(
				request.orderItems().stream()
					.map(i -> CreateOrderCommand.OrderItemCommand.builder()
						.timeDealStockId(UUID.fromString(i.timeDealStockId()))
						.quantity(i.quantity())
						.build())
					.toList()
			)
			.pointUsed(request.pointUsed())
			.shippingInfo(toShippingInfo(request.shippingInfo()))
			.build();
	}

	private ShippingInfo toShippingInfo(CreateOrderRequest.ShippingInfoRequest info) {
		return ShippingInfo.builder()
			.recipientName(info.recipientName())
			.recipientPhone(info.recipientPhone())
			.zipCode(info.zipCode())
			.addressBase(info.addressBase())
			.addressDetail(info.addressDetail())
			.deliveryMessage(info.deliveryMessage())
			.build();
	}
}
