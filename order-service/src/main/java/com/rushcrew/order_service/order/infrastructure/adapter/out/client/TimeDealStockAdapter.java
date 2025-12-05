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

	@Override
	public TimeDealStockDetail getTimeDealStockDetail(@NonNull UUID timeDealStockId) {
		TimeDealStockDetailResponse response = feignClient.getTimeDealStockDetail(timeDealStockId.toString());
		TimeDealStatus appStatus = mapStatus(response.getStatus());

		return TimeDealStockDetail.builder()
			.timeDealStockId(UUID.fromString(response.getTimeDealStockId()))
			.timeDealId(UUID.fromString(response.getTimeDealId()))
			.availableStock(response.getAvailableStock())
			.reservedStock(response.getReservedStock())
			.soldStock(response.getSoldStock())
			.status(appStatus)
			.productId(UUID.fromString(response.getProductId()))
			.productName(response.getProductName())
			.productPrice(response.getProductPrice())
			.productDescription(response.getProductDescription())
			.category(response.getCategory())
			.optionId(UUID.fromString(response.getOptionId()))
			.optionName(response.getOptionName())
			.sellerId(UUID.fromString(response.getSellerId()))
			.isActive(response.isActive())
			.build();
	}

	@Override
	public StockReservationResult reserveStock(
		@NonNull UUID timeDealStockId,
		@NonNull Integer quantity,
		@NonNull Long userId
	) {
		StockReservationRequest request = StockReservationRequest.builder()
			.timeDealStockId(timeDealStockId.toString())
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
	public void confirmStock(
		@NonNull UUID timeDealStockId,
		@NonNull Integer quantity,
		@NonNull UUID orderId
	) {
		StockConfirmRequest request = StockConfirmRequest.builder()
			.timeDealStockId(timeDealStockId.toString())
			.quantity(quantity)
			.orderId(orderId.toString())
			.build();
		feignClient.confirmStock(request);
	}

	@Override
	public void restoreStock(
		@NonNull UUID timeDealStockId,
		@NonNull Integer quantity,
		@NonNull UUID orderId,
		@NonNull String reason
	) {
		StockRestoreRequest request = StockRestoreRequest.builder()
			.timeDealStockId(timeDealStockId.toString())
			.quantity(quantity)
			.orderId(orderId.toString())
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
