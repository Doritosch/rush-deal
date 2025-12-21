package com.rushcrew.order_service.presentation.api.command;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.rushcrew.order_service.application.command.mapper.CancelOrderCommandMapper;
import com.rushcrew.order_service.application.command.mapper.CancelOrderResultMapper;
import com.rushcrew.order_service.application.command.mapper.RefundOrderCommandMapper;
import com.rushcrew.order_service.application.command.mapper.RefundOrderResultMapper;
import com.rushcrew.order_service.application.command.usecase.CancelOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.ConfirmPurchaseUseCase;
import com.rushcrew.order_service.application.command.usecase.CreateOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.RefundOrderUseCase;
import com.rushcrew.order_service.application.command.usecase.RequestPaymentUseCase;
import com.rushcrew.order_service.application.command.usecase.UpdateOrderUseCase;
import com.rushcrew.order_service.application.command.mapper.ConfirmPurchaseCommandMapper;
import com.rushcrew.order_service.application.command.mapper.ConfirmPurchaseResultMapper;
import com.rushcrew.order_service.application.command.mapper.CreateOrderCommandMapper;
import com.rushcrew.order_service.application.command.mapper.RequestPaymentCommandMapper;
import com.rushcrew.order_service.application.command.mapper.RequestPaymentResultMapper;
import com.rushcrew.order_service.application.command.mapper.UpdateOrderCommandMapper;
import com.rushcrew.order_service.application.command.mapper.UpdateOrderResultMapper;
import com.rushcrew.order_service.global.util.RoleChecker;
import com.rushcrew.order_service.presentation.dto.request.CreateOrderRequest;
import com.rushcrew.order_service.presentation.dto.request.UpdateOrderRequest;
import com.rushcrew.order_service.presentation.dto.response.CancelOrderResponse;
import com.rushcrew.order_service.presentation.dto.response.ConfirmPurchaseResponse;
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
	private final RequestPaymentUseCase requestPaymentUseCase;
	private final RequestPaymentCommandMapper requestPaymentCommandMapper;
	private final RequestPaymentResultMapper requestPaymentResultMapper;
	private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
	private final ConfirmPurchaseCommandMapper confirmPurchaseCommandMapper;
	private final ConfirmPurchaseResultMapper confirmPurchaseResultMapper;
	private final UpdateOrderUseCase updateOrderUseCase;
	private final UpdateOrderCommandMapper updateOrderCommandMapper;
	private final UpdateOrderResultMapper updateOrderResultMapper;
	private final CancelOrderUseCase cancelOrderUseCase;
	private final CancelOrderCommandMapper cancelOrderCommandMapper;
	private final CancelOrderResultMapper cancelOrderResultMapper;
	private final RefundOrderUseCase refundOrderUseCase;
	private final RefundOrderCommandMapper refundOrderCommandMapper;
	private final RefundOrderResultMapper refundOrderResultMapper;

	/**
	 * 주문 생성 API (Saga 접수)
	 */
	@PostMapping
	public ApiResponse<CreateOrderResult> createOrder(
		@Valid @RequestBody CreateOrderRequest request,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		@RequestHeader("X-Queue-Token") String queueToken
	) {
		// 권한 체크
		RoleChecker.checkRole(role, "USER", "MASTER", "SELLER");
		// DTO -> Command 변환
		CreateOrderCommand command = createOrderCommandMapper.toCommand(request, userId, role, queueToken);
		// Saga 접수
		CreateOrderResult result = createOrderUseCase.createOrder(command);
		// 반환 (PROCESSING 상태 + sagaId)
		return ApiResponse.success(result);
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
		ConfirmPurchaseCommand command = confirmPurchaseCommandMapper.toCommand(orderId, userId);
		ConfirmPurchaseResult result = confirmPurchaseUseCase.confirmPurchase(command);
		ConfirmPurchaseResponse response = confirmPurchaseResultMapper.toResponse(result);
		return ApiResponse.success(response);
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
		UpdateOrderCommand command = updateOrderCommandMapper.toCommand(orderId, userId, request);
		UpdateOrderResult result = updateOrderUseCase.updateOrder(command);
		UpdateOrderResponse response = updateOrderResultMapper.toResponse(result);
		return ApiResponse.success(response);
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
		CancelOrderCommand command = cancelOrderCommandMapper.toCommand(orderId, userId);
		CancelOrderResult result = cancelOrderUseCase.cancelOrder(command);
		CancelOrderResponse response = cancelOrderResultMapper.toResponse(result);
		return ApiResponse.success(response);
	}

	/**
	 * 주문 환불 API
	 */
	@PostMapping("/{orderId}/refund")
	public ApiResponse<RefundOrderResponse> refundOrder(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		@RequestParam(value = "reason", required = false) String reason
	) {
		RoleChecker.checkRole(role, "USER", "MASTER");
		RefundOrderCommand command = refundOrderCommandMapper.toCommand(orderId, userId, reason);
		RefundOrderResult result = refundOrderUseCase.refundOrder(command);
		RefundOrderResponse response = refundOrderResultMapper.toResponse(result);
		return ApiResponse.success(response);
	}

}
