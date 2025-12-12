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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService implements GetOrderDetailUseCase, GetOrderListUseCase {

	private final OrderQueryPort orderQueryPort;

	@Override
	public OrderDetailDto getOrderDetail(UUID orderId, Long userId) {
		log.info("주문 상세 조회: orderId={}, userId={}", orderId, userId);
		OrderDetailDto dto = orderQueryPort.findById(orderId)
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
		if (!dto.getUserId().equals(userId)) {
			throw new BusinessException(OrderErrorCode.UNAUTHORIZED);
		}
		return dto;
	}

	@Override
	public Page<OrderListDto> getOrderList(OrderSearchCriteria criteria, Pageable pageable) {
		log.info("주문 목록 조회: userId={}", criteria.getUserId());
		return orderQueryPort.findByCriteria(criteria, pageable);
	}
}
