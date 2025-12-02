package com.rushcrew.order_service.order.presentation;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.order_service.global.util.JwtUtils;
import com.rushcrew.order_service.order.application.command.CreateOrderCommand;
import com.rushcrew.order_service.order.application.command.CreateOrderResult;
import com.rushcrew.order_service.order.application.handler.CreateOrderCommandHandler;
import com.rushcrew.order_service.order.presentation.dto.request.CreateOrderRequest;
import com.rushcrew.order_service.order.presentation.dto.response.CreateOrderResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final CreateOrderCommandHandler createOrderCommandHandler;

	@PostMapping
	// @PreAuthorize("hasAnyRole('USER', 'MASTER', 'SELLER')")
	public ApiResponse<CreateOrderResponse> createOrder(
		@Valid @RequestBody CreateOrderRequest request
	) {
		Long userId = JwtUtils.getCurrentUserId();
		// String role = JwtUtils.getCurrentUserRole();

		CreateOrderCommand command = CreateOrderCommand.builder()
			.userId(userId)
			.timeDealId(request.getTimeDealId())
			.orderItems(request.getOrderItems().stream()
				.map(item -> CreateOrderCommand.OrderItemCommand.builder()
					.timeDealStockId(item.getTimeDealStockId())
					.quantity(item.getQuantity())
					.build())
				.collect(Collectors.toList()))
			.pointUsed(request.getPointUsed())
			.shippingInfo(request.getShippingInfo().toShippingInfo())
			.build();

		CreateOrderResult result = createOrderCommandHandler.handle(command);

		CreateOrderResponse response = CreateOrderResponse.from(result);

		return ApiResponse.success(response);
	}

}
