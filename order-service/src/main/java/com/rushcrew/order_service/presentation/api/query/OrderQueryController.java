package com.rushcrew.order_service.presentation.api.query;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rushcrew.common.dto.ApiResponse;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.application.query.dto.OrderListDto;
import com.rushcrew.order_service.application.query.dto.OrderSearchCriteria;
import com.rushcrew.order_service.application.query.usecase.GetOrderDetailUseCase;
import com.rushcrew.order_service.application.query.usecase.GetOrderListUseCase;
import com.rushcrew.order_service.global.util.RoleChecker;
import com.rushcrew.order_service.presentation.dto.response.OrderDetailResponse;
import com.rushcrew.order_service.presentation.dto.response.OrderListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderQueryController {

	private final GetOrderDetailUseCase getOrderDetailUseCase;
	private final GetOrderListUseCase getOrderListUseCase;

	@GetMapping("/{orderId}")
	@PreAuthorize("hasAnyRole('USER', 'MASTER', 'SELLER')")
	public ApiResponse<OrderDetailResponse> getOrderDetail(
		@PathVariable UUID orderId,
		@RequestHeader("X-User-Id") Long userId
	) {
		OrderDetailDto dto = getOrderDetailUseCase.getOrderDetail(orderId, userId);

		return ApiResponse.success(OrderDetailResponse.from(dto));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'MASTER')")
	public ApiResponse<Page<OrderListResponse>> getOrderList(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader(value = "X-User-Role", required = false) String role,
		Pageable pageable
	) {
		OrderSearchCriteria criteria = OrderSearchCriteria.builder()
			.userId(userId)
			.build();

		Page<OrderListDto> dtos = getOrderListUseCase.getOrderList(criteria, pageable);

		return ApiResponse.success(dtos.map(OrderListResponse::from));
	}
}
