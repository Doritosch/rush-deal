package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.rushcrew.order_service.infrastructure.dto.point.PointDeductRequest;
import com.rushcrew.order_service.infrastructure.dto.point.PointDeductResponse;

@FeignClient(name = "user-service")
public interface PointFeignClient {
	// TODO: USER 서비스 API 확인
	@PostMapping("/api/v1/points/deduct")
	PointDeductResponse deductPoint(PointDeductRequest request);
}
