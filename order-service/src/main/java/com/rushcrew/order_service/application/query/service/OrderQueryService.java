package com.rushcrew.order_service.application.query.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.application.query.dto.OrderListDto;
import com.rushcrew.order_service.application.query.dto.OrderSearchCriteria;
import com.rushcrew.order_service.application.query.usecase.GetOrderDetailUseCase;
import com.rushcrew.order_service.application.query.usecase.GetOrderListUseCase;
import com.rushcrew.order_service.application.query.port.out.OrderQueryPort;
import com.rushcrew.order_service.global.error.OrderErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements GetOrderDetailUseCase, GetOrderListUseCase {

	private final OrderQueryPort orderQueryPort;

	@Override
	public OrderDetailDto getOrderDetail(UUID orderId, Long userId) {
		OrderDetailDto dto = orderQueryPort.findById(orderId)
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
		if (!dto.getUserId().equals(userId)) {
			throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
		}
		return dto;
	}

	@Override
	public Page<OrderListDto> getOrderList(OrderSearchCriteria criteria, Pageable pageable) {
		return orderQueryPort.findByCriteria(criteria, pageable);
	}
}
