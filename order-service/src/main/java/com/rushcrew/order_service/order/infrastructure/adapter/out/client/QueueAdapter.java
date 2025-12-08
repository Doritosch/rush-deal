package com.rushcrew.order_service.order.infrastructure.adapter.out.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.QueuePort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign.QueueFeignClient;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueAdapter implements QueuePort {

	private final QueueFeignClient feignClient;

	@Override
	public boolean validateQueueToken(
		@NonNull UUID timeDealId,
		@NonNull Long userId
	) {
		return feignClient.validateQueueToken(timeDealId.toString(), userId);
	}

	@Override
	public void extendTokenTtl(
		@NonNull UUID timeDealId,
		@NonNull Long userId,
		int seconds
	) {
		feignClient.extendTokenTtl(timeDealId.toString(), userId, seconds);
	}

	@Override
	public void removeUserToken(
		@NonNull UUID timeDealId,
		@NonNull Long userId
	) {
		feignClient.removeUserToken(timeDealId.toString(), userId);
	}
}
