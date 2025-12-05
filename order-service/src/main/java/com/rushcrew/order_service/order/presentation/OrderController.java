package com.rushcrew.order_service.order.presentation;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.order_service.global.util.RoleChecker;
import com.rushcrew.order_service.order.application.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.order.application.command.ConfirmPurchaseResult;
import com.rushcrew.order_service.order.application.command.CreateOrderCommand;
import com.rushcrew.order_service.order.application.command.CreateOrderResult;
import com.rushcrew.order_service.order.application.command.RequestPaymentCommand;
import com.rushcrew.order_service.order.application.command.RequestPaymentResult;
import com.rushcrew.order_service.order.application.handler.ConfirmPurchaseCommandHandler;
import com.rushcrew.order_service.order.application.handler.CreateOrderCommandHandler;
import com.rushcrew.order_service.order.application.handler.RequestPaymentCommandHandler;
import com.rushcrew.order_service.order.presentation.dto.request.CreateOrderRequest;
import com.rushcrew.order_service.order.presentation.dto.request.RequestPaymentRequest;
import com.rushcrew.order_service.order.presentation.dto.response.ConfirmPurchaseResponse;
import com.rushcrew.order_service.order.presentation.dto.response.CreateOrderResponse;
import com.rushcrew.order_service.order.presentation.dto.response.RequestPaymentResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final CreateOrderCommandHandler createOrderCommandHandler;
	private final RequestPaymentCommandHandler requestPaymentCommandHandler;
	private final ConfirmPurchaseCommandHandler confirmPurchaseCommandHandler;

	/*
	* 주문 생성 API
	* */
	@PostMapping
	public ApiResponse<CreateOrderResponse> createOrder(
		@Valid @RequestBody CreateOrderRequest request,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER", "SELLER");
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
			.paymentMethod(request.getPaymentMethod())
			.build();

		CreateOrderResult result = createOrderCommandHandler.handle(command);
		CreateOrderResponse response = CreateOrderResponse.from(result);

		return ApiResponse.success(response);
	}


	/*
	* 결제 요청 API
	* */
	@PostMapping("/{orderId}/payment")
	public ApiResponse<RequestPaymentResponse> requestPayment(
		@PathVariable UUID orderId,
		@Valid @RequestBody RequestPaymentRequest request,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	){
		RoleChecker.checkRole(role, "USER", "MASTER");
		RequestPaymentCommand command = new RequestPaymentCommand(orderId, userId, request.getPaymentMethod());
		RequestPaymentResult result = requestPaymentCommandHandler.handle(command);
		RequestPaymentResponse response = RequestPaymentResponse.from(result);

		return ApiResponse.success(response);
	}

	/*
	* 구매확정 API
	* */
	@PostMapping("/{orderId}/confirm")
	public ApiResponse<ConfirmPurchaseResponse> confirmPurchase(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");
		ConfirmPurchaseCommand command = new ConfirmPurchaseCommand(orderId, userId);
		ConfirmPurchaseResult result = confirmPurchaseCommandHandler.handle(command);
		ConfirmPurchaseResponse response = ConfirmPurchaseResponse.from(result);

		return ApiResponse.success(response);
	}

}
