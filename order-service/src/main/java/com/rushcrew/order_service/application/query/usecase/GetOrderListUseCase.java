package com.rushcrew.order_service.application.query.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rushcrew.order_service.application.query.dto.OrderListDto;
import com.rushcrew.order_service.application.query.dto.OrderSearchCriteria;

public interface GetOrderListUseCase {
	Page<OrderListDto> getOrderList(OrderSearchCriteria criteria, Pageable pageable);
}
