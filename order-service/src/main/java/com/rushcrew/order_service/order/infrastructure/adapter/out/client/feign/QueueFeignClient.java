package com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "queue-service")
public interface QueueFeignClient {

	@GetMapping("/api/v1/queue/{timeDealId}/validate")
	boolean validateQueueToken(
		@PathVariable("timeDealId") String timeDealId,
		@RequestParam("userId") Long userId
	);

	@PostMapping("/api/v1/queue/{timeDealId}/extend")
	void extendTokenTtl(
		@PathVariable("timeDealId") String timeDealId,
		@RequestParam("userId") Long userId,
		@RequestParam("seconds") int seconds
	);

	@DeleteMapping("/api/v1/queue/{timeDealId}/users/{userId}")
	void removeUserToken(
		@PathVariable("timeDealId") String timeDealId,
		@PathVariable("userId") Long userId
	);

}
