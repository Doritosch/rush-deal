package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.NonNull;

// TODO: 송경님 API 생성 요청 -> 통합 테스트 시 불필요하면 제거

@FeignClient(name = "queue-service")
public interface QueueFeignClient {
	@GetMapping("/api/v1/queue/validate")
	boolean validateToken(String timeDealId, @NonNull Long userId);
}
