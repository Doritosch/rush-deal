package com.rushcrew.order.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rushcrew.order.infrastructure.client.QueueServiceClient;

@FeignClient(name = "queue-service")
public interface QueuFeignClient extends QueueServiceClient {

	@GetMapping("/api/v1/queue/{timeDealId}/validate")
	@Override
	boolean validateQueueToken(
		@PathVariable("timeDealId") String timeDealId,
		@RequestParam("userId") Long userId
	);

	@PostMapping("/api/v1/queue/{timeDealId}/extend")
	@Override
	void extendTokenTtl(
		@PathVariable("timeDealId") String timeDealId,
		@RequestParam("userId") Long userId,
		@RequestParam("seconds") int seconds
	);

	@DeleteMapping("/api/v1/queue/{timeDealId}/users/{userId}")
	@Override
	void removeUserToken(
		@PathVariable("timeDealId") String timeDealId,
		@PathVariable("userId") Long userId
	);

}
