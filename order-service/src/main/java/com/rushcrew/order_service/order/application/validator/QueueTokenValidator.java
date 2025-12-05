package com.rushcrew.order_service.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.out.QueuePort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenValidator {
	private final QueuePort queueServiceClient;

	public void validate(String timeDealId, Long userId) {
		if (!queueServiceClient.validateQueueToken(timeDealId, userId)) {
			throw new BusinessException(OrderErrorCode.INVALID_QUEUE_TOKEN);
		}
	}
}
