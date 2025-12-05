package com.rushcrew.order_service.order.infrastructure.adapter.out.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.dto.StockReservationResult;
import com.rushcrew.order_service.order.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStatus;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStockDetail;
import com.rushcrew.order_service.order.application.port.out.TimeDealStockPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign.TimeDealStockFeignClient;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockConfirmRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockReservationRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockReservationResponse;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockRestoreRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.TimeDealResponse;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.TimeDealResponseStatus;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.TimeDealStockDetailResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeDealStockAdapter implements TimeDealStockPort {

	private final TimeDealStockFeignClient feignClient;

	@Override
	public TimeDealInfo getTimeDeal(String timeDealId) {
		TimeDealResponse response = feignClient.getTimeDeal(timeDealId);
		TimeDealStatus appStatus = mapStatus(response.getStatus());

		return TimeDealInfo.builder()
			.timeDealId(response.getTimeDealId())
			.title(response.getTitle())
			.status(appStatus)
			.discountPrice(response.getDiscountPrice())
			.discountRate(response.getDiscountRate())
			.limitQuantity(response.getLimitQuantity())
			.build();
	}

	@Override
	public TimeDealStockDetail getTimeDealStockDetail(UUID timeDealStockId) {
		TimeDealStockDetailResponse response = feignClient.getTimeDealStockDetail(timeDealStockId);
		TimeDealStatus appStatus = mapStatus(response.getStatus());

		return TimeDealStockDetail.builder()
			.timeDealStockId(response.getTimeDealStockId())
			.timeDealId(response.getTimeDealId())
			.availableStock(response.getAvailableStock())
			.reservedStock(response.getReservedStock())
			.soldStock(response.getSoldStock())
			.status(appStatus)
			.productId(response.getProductId())
			.productName(response.getProductName())
			.productPrice(response.getProductPrice())
			.productDescription(response.getProductDescription())
			.category(response.getCategory())
			.optionId(response.getOptionId())
			.optionName(response.getOptionName())
			.sellerId(response.getSellerId())
			.isActive(response.isActive())
			.build();
	}

	@Override
	public StockReservationResult reserveStock(UUID timeDealStockId, Integer quantity, Long userId) {
		StockReservationRequest request = StockReservationRequest.builder()
			.timeDealStockId(timeDealStockId)
			.quantity(quantity)
			.userId(userId)
			.build();

		StockReservationResponse response = feignClient.reserveStock(request);
		if (response.isSuccess()) {
			return StockReservationResult.success(timeDealStockId, quantity);
		} else {
			return StockReservationResult.failure(
				response.getAvailableStock(), response.getMessage()
			);
		}
	}

	@Override
	public void confirmStock(UUID timeDealStockId, Integer quantity, String orderId) {
		StockConfirmRequest request = StockConfirmRequest.builder()
			.timeDealStockId(timeDealStockId)
			.quantity(quantity)
			.orderId(orderId)
			.build();
		feignClient.confirmStock(request);
	}

	@Override
	public void restoreStock(UUID timeDealStockId, Integer quantity, String orderId, String reason) {
		StockRestoreRequest request = StockRestoreRequest.builder()
			.timeDealStockId(timeDealStockId)
			.quantity(quantity)
			.orderId(orderId)
			.reason(reason)
			.build();
		feignClient.restoreStock(request);
	}

	private TimeDealStatus mapStatus(TimeDealResponseStatus infraStatus) {
		switch (infraStatus) {
			case SCHEDULED: return TimeDealStatus.SCHEDULED;
			case IN_PROGRESS: return TimeDealStatus.IN_PROGRESS;
			case SOLD_OUT: return TimeDealStatus.SOLD_OUT;
			case ENDED: return TimeDealStatus.ENDED;
			default: throw new IllegalArgumentException("존재하지 않는 타임딜 상태: " + infraStatus);
		}
	}
}
