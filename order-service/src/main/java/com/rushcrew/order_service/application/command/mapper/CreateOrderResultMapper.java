package com.rushcrew.order_service.application.command.mapper;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.presentation.dto.response.CreateOrderResponse;
import com.rushcrew.order_service.presentation.dto.response.CreateOrderResponse.OrderItemResponse;

@Component
public class CreateOrderResultMapper {

	public CreateOrderResponse toResponse(CreateOrderResult result) {
		return new CreateOrderResponse(
			result.orderId(),
			result.orderStatus(),
			result.totalAmount(),
			result.pointUsed(),
			result.finalAmount(),
			result.orderedAt(),
			result.reservationExpiresAt(),
			"주문이 성공적으로 생성되었습니다. 15분 내에 결제를 완료해주세요.",
			result.orderItems().stream()
				.map(item -> new OrderItemResponse(
					item.orderItemId(),
					item.productName(),
					item.optionName(),
					item.quantity(),
					item.unitPrice(),
					item.discountPrice(),
					item.subtotal()
				))
				.toList()
		);
	}
}
