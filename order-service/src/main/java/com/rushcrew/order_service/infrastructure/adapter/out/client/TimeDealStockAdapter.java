package com.rushcrew.order_service.infrastructure.adapter.out.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.application.port.dto.TimeDealStatus;
import com.rushcrew.order_service.application.port.out.TimeDealStockPort;

import com.rushcrew.order_service.infrastructure.adapter.out.client.feign.TimeDealStockFeignClient;
import com.rushcrew.order_service.infrastructure.dto.timedeal.TimeDealResponse;
import com.rushcrew.order_service.infrastructure.dto.timedeal.TimeDealStatusResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeDealStockAdapter implements TimeDealStockPort {

	private final TimeDealStockFeignClient feignClient;

	@Override
	public TimeDealInfo getTimeDeal(@NonNull UUID timeDealId) {
		TimeDealResponse response = feignClient.getTimeDeal(timeDealId.toString());
		TimeDealStatus appStatus = mapStatus(response.getStatus());
		return TimeDealInfo.builder()
			.timeDealId(UUID.fromString(response.getTimeDealId()))
			.title(response.getTitle())
			.status(appStatus)
			.discountPrice(response.getDiscountPrice())
			.discountRate(response.getDiscountRate())
			.limitQuantity(response.getLimitQuantity())
			.build();
	}

	private TimeDealStatus mapStatus(TimeDealStatusResponse infraStatus) {
		switch (infraStatus) {
			case SCHEDULED: return TimeDealStatus.SCHEDULED;
			case IN_PROGRESS: return TimeDealStatus.IN_PROGRESS;
			case SOLD_OUT: return TimeDealStatus.SOLD_OUT;
			case ENDED: return TimeDealStatus.ENDED;
			default: throw new IllegalArgumentException("존재하지 않는 타임딜 상태: " + infraStatus);
		}
	}
}
