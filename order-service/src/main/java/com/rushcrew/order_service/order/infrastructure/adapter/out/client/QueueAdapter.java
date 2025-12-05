package com.rushcrew.order_service.order.infrastructure.adapter.out.client;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.QueuePort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign.QueueFeignClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueAdapter implements QueuePort {

	private final QueueFeignClient feignClient;

	@Override
	public boolean validateQueueToken(String timeDealId, Long userId) {
		return feignClient.validateQueueToken(timeDealId, userId);
	}

	@Override
	public void extendTokenTtl(String timeDealId, Long userId, int seconds) {
		feignClient.extendTokenTtl(timeDealId, userId, seconds);
	}

	@Override
	public void removeUserToken(String timeDealId, Long userId) {
		feignClient.removeUserToken(timeDealId, userId);
	}
}
