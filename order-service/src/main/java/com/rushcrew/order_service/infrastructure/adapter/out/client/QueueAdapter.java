package com.rushcrew.order_service.infrastructure.adapter.out.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.QueuePort;
import com.rushcrew.order_service.infrastructure.adapter.out.client.feign.QueueFeignClient;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueAdapter implements QueuePort {

	private final QueueFeignClient feignClient;

	@Override
	public boolean validateToken(@NonNull UUID timeDealId, @NonNull Long userId) {
		try {
			return feignClient.validateToken(timeDealId.toString(), userId);
		} catch (Exception e) {
			log.error("대기열 토큰 검증 실패: timeDealId={}, userId={}", timeDealId, userId, e);
			return false;
		}
	}
}
