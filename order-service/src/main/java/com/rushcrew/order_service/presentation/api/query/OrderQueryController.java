package com.rushcrew.order_service.presentation.api.query;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.application.query.dto.OrderListDto;
import com.rushcrew.order_service.application.query.dto.OrderSearchCriteria;
import com.rushcrew.order_service.application.query.usecase.GetOrderDetailUseCase;
import com.rushcrew.order_service.application.query.usecase.GetOrderListUseCase;
import com.rushcrew.order_service.presentation.dto.response.OrderDetailResponse;
import com.rushcrew.order_service.presentation.dto.response.OrderListResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderQueryController {

	private final GetOrderDetailUseCase getOrderDetailUseCase;
	private final GetOrderListUseCase getOrderListUseCase;

	@GetMapping("/{orderId}")
	public ApiResponse<OrderDetailResponse> getOrderDetail(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role) {

		boolean isMaster = "MASTER".equalsIgnoreCase(role);
		OrderDetailDto order = getOrderDetailUseCase.getOrderDetail(orderId, userId, isMaster);
		return ApiResponse.success(OrderDetailResponse.from(order));
	}

	@GetMapping
	public ApiResponse<Page<OrderListResponse>> getOrderList(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		Pageable pageable) {

		boolean isMaster = "MASTER".equalsIgnoreCase(role);
		OrderSearchCriteria criteria = OrderSearchCriteria.builder()
			.userId(isMaster ? null : userId)
			.build();

		Page<OrderListDto> orders = getOrderListUseCase.getOrderList(criteria, pageable);
		return ApiResponse.success(orders.map(OrderListResponse::from));
	}
}
