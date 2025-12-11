package com.rushcrew.order_service.presentation.api.command;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.order_service.application.command.dto.command.CancelOrderCommand;
import com.rushcrew.order_service.application.command.dto.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;
import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;
import com.rushcrew.order_service.application.command.dto.command.UpdateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CancelOrderResult;
import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;
import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;
import com.rushcrew.order_service.application.command.dto.result.UpdateOrderResult;
import com.rushcrew.order_service.application.command.usecase.CancelOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.ConfirmPurchaseUseCase;
import com.rushcrew.order_service.application.command.usecase.CreateOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.RefundOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.RequestPaymentUseCase;
import com.rushcrew.order_service.application.command.usecase.UpdateOrderUseCase;
import com.rushcrew.order_service.application.mapper.CreateOrderCommandMapper;
import com.rushcrew.order_service.application.mapper.CreateOrderResultMapper;
import com.rushcrew.order_service.application.mapper.RequestPaymentCommandMapper;
import com.rushcrew.order_service.application.mapper.RequestPaymentResultMapper;
import com.rushcrew.order_service.global.util.RoleChecker;
import com.rushcrew.order_service.presentation.dto.request.CreateOrderRequest;
import com.rushcrew.order_service.presentation.dto.request.UpdateOrderRequest;
import com.rushcrew.order_service.presentation.dto.response.CancelOrderResponse;
import com.rushcrew.order_service.presentation.dto.response.ConfirmPurchaseResponse;
import com.rushcrew.order_service.presentation.dto.response.CreateOrderResponse;
import com.rushcrew.order_service.presentation.dto.response.RefundOrderResponse;
import com.rushcrew.order_service.presentation.dto.response.RequestPaymentResponse;
import com.rushcrew.order_service.presentation.dto.response.UpdateOrderResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderCommandController {

	private final CreateOrderUseCase createOrderUseCase;
	private final CreateOrderCommandMapper createOrderCommandMapper;
	private final CreateOrderResultMapper createOrderResultMapper;
	private final RequestPaymentUseCase requestPaymentUseCase;
	private final RequestPaymentCommandMapper requestPaymentCommandMapper;
	private final RequestPaymentResultMapper requestPaymentResultMapper;
	private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
	private final UpdateOrderUseCase updateOrderUseCase;
	private final CancelOrderUseCase cancelOrderUseCase;
	private final RefundOrderUseCase refundOrderUseCase;

	/**
	 * 주문 생성 API
	 */
	@PostMapping
	public ApiResponse<CreateOrderResponse> createOrder(
		@Valid @RequestBody CreateOrderRequest request,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		@RequestHeader("X-Queue-Token") String queueToken
	) {
		RoleChecker.checkRole(role, "USER", "MASTER", "SELLER");
		CreateOrderCommand command = createOrderCommandMapper.toCommand(request, userId, role, queueToken);
		CreateOrderResult result = createOrderUseCase.createOrder(command);
		CreateOrderResponse response = createOrderResultMapper.toResponse(result);
		return ApiResponse.success(response);
	}

	/**
	 * 결제 요청 API
	 */
	@PostMapping("/{orderId}/payment")
	public ApiResponse<RequestPaymentResponse> requestPayment(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");
		RequestPaymentCommand command = requestPaymentCommandMapper.toCommand(orderId, userId);
		RequestPaymentResult result = requestPaymentUseCase.requestPayment(command);
		RequestPaymentResponse response = requestPaymentResultMapper.toResponse(result);
		return ApiResponse.success(response);
	}

	/**
	 * 구매확정 API
	 */
	@PostMapping("/{orderId}/confirm")
	public ApiResponse<ConfirmPurchaseResponse> confirmPurchase(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");

		ConfirmPurchaseCommand command = ConfirmPurchaseCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.build();

		ConfirmPurchaseResult result = confirmPurchaseUseCase.confirmPurchase(command);

		return ApiResponse.success(ConfirmPurchaseResponse.from(result));
	}

	/**
	 * 주문 수정 API
	 */
	@PutMapping("/{orderId}")
	public ApiResponse<UpdateOrderResponse> updateOrder(
		@PathVariable UUID orderId,
		@Valid @RequestBody UpdateOrderRequest request,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");

		UpdateOrderCommand command = UpdateOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.shippingInfo(request.shippingInfo() != null ? request.shippingInfo().toShippingInfo() : null)
			.pointUsed(request.pointUsed())
			.build();

		UpdateOrderResult result = updateOrderUseCase.updateOrder(command);

		return ApiResponse.success(UpdateOrderResponse.from(result));
	}

	/**
	 * 주문 취소 API
	 */
	@PostMapping("/{orderId}/cancel")
	public ApiResponse<CancelOrderResponse> cancelOrder(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.build();

		CancelOrderResult result = cancelOrderUseCase.cancelOrder(command);

		return ApiResponse.success(CancelOrderResponse.from(result));
	}

	/**
	 * 주문 환불 API
	 */
	@PostMapping("/{orderId}/refund")
	public ApiResponse<RefundOrderResponse> refundOrder(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		@RequestBody(required = false) java.util.Map<String, String> requestBody
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");

		String reason = requestBody != null ? requestBody.get("reason") : null;

		RefundOrderCommand command = RefundOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.reason(reason)
			.build();

		RefundOrderResult result = refundOrderUseCase.refundOrder(command);

		return ApiResponse.success(RefundOrderResponse.from(result));
	}

}
