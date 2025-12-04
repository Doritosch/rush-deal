package com.rushcrew.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.InvalidQueueTokenException;
import com.rushcrew.order.application.port.out.QueuePort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenValidator {
	private final QueuePort queueServiceClient;

	public void validate(String timeDealId, Long userId) {
		if (!queueServiceClient.validateQueueToken(timeDealId, userId)) {
			throw new InvalidQueueTokenException();
		}
	}
}
