package com.rushcrew.order_service.infrastructure.adapter.out.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.PointPort;
import com.rushcrew.order_service.infrastructure.adapter.out.client.feign.PointFeignClient;
import com.rushcrew.order_service.infrastructure.dto.point.PointDeductRequest;
import com.rushcrew.order_service.infrastructure.dto.point.PointDeductResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointAdapter implements PointPort {

	private final PointFeignClient feignClient;

	@Override
	public boolean deductPoint(@NonNull Long userId, @NonNull BigDecimal amount, @NonNull UUID sagaId,
		@NonNull String reason) {

		try {
			log.info("포인트 차감 요청: userId={}, amount={}, sagaId={}", userId, amount, sagaId);

			PointDeductRequest request = PointDeductRequest.builder()
				.userId(userId)
				.amount(amount)
				.sagaId(sagaId.toString())
				.reason(reason)
				.build();

			PointDeductResponse response = feignClient.deductPoint(request);

			if (response.isSuccess()) {
				log.info("포인트 차감 성공: userId={}, amount={}, remainingPoint={}",
					userId, amount, response.getRemainingPoint());
				return true;
			} else {
				log.warn("포인트 차감 실패: userId={}, message={}", userId, response.getMessage());
				return false;
			}

		} catch (Exception e) {
			log.error("포인트 차감 중 오류 발생: userId={}, amount={}", userId, amount, e);
			throw new RuntimeException("포인트 차감 실패", e);
		}
	}
}
