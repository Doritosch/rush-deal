package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rushcrew.order_service.infrastructure.dto.point.PointDeductRequest;
import com.rushcrew.order_service.infrastructure.dto.point.PointDeductResponse;
import com.rushcrew.order_service.infrastructure.dto.point.PointRefundRequest;

// TODO: 영재님 API 확인 요청

@FeignClient(name = "user-service")
public interface PointFeignClient {

	@PostMapping("/api/v1/points/deduct")
	PointDeductResponse deductPoint(@RequestBody PointDeductRequest request);

	@PostMapping("/api/v1/points/refund")
	void refundPoint(@RequestBody PointRefundRequest request);
}
