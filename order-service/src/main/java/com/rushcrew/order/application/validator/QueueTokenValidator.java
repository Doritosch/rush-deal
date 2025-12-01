package com.rushcrew.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.InvalidQueueTokenException;
import com.rushcrew.order.infrastructure.client.QueueServiceClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenValidator {
	private final QueueServiceClient queueServiceClient;

	public void validate(String timeDealId, Long userId) {
		if (!queueServiceClient.validateQueueToken(timeDealId, userId)) {
			throw new InvalidQueueTokenException();
		}
	}
}
