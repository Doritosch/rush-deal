package com.rushcrew.order_service.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStatus;

@Component
public class TimeDealValidator {

	public void validate(TimeDealInfo timeDeal) {
		if (timeDeal.getStatus() != TimeDealStatus.IN_PROGRESS) {
			throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
		}
	}
}
