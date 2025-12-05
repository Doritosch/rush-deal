package com.rushcrew.order_service.order.application.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.command.CreateOrderCommand;
import com.rushcrew.order_service.order.application.command.CreateOrderResult;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.dto.StockReservationResult;
import com.rushcrew.order_service.order.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStockDetail;
import com.rushcrew.order_service.order.application.validator.OrderItemValidator;
import com.rushcrew.order_service.order.application.validator.PurchaseLimitValidator;
import com.rushcrew.order_service.order.application.validator.QueueTokenValidator;
import com.rushcrew.order_service.order.application.validator.TimeDealStockValidator;
import com.rushcrew.order_service.order.application.validator.TimeDealValidator;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.entity.OrderItem;
import com.rushcrew.order_service.order.domain.entity.OrderReservation;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;
import com.rushcrew.order_service.order.domain.vo.ProductSnapshot;
import com.rushcrew.order_service.order.application.port.out.QueuePort;
import com.rushcrew.order_service.order.application.port.out.TimeDealStockPort;
import com.rushcrew.order_service.order.application.port.out.OrderEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.lock.DistributedLockManager;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreationService {

	// Repositories
	private final OrderRepository orderRepository;

	// Port
	private final TimeDealStockPort timeDealStockPort;
	private final QueuePort queuePort;
	private final OrderEventPort orderEventPort;

	// Infrastructure
	private final DistributedLockManager lockManager;

	// Validators
	private final QueueTokenValidator queueTokenValidator;
	private final TimeDealValidator timeDealValidator;
	private final OrderItemValidator orderItemValidator;
	private final PurchaseLimitValidator purchaseLimitValidator;
	private final TimeDealStockValidator timeDealStockValidator;


	/* 주문 생성 */
	@Transactional
	public CreateOrderResult createOrder(CreateOrderCommand command) {

		// 1. 대기열 토큰 검증
		queueTokenValidator.validate(command.getTimeDealId(), command.getUserId());
		// 2. 타임딜 정보 조회 및 검증
		TimeDealInfo timeDeal = timeDealStockPort.getTimeDeal(command.getTimeDealId());
		timeDealValidator.validate(timeDeal);
		// 3. 중복 상품 검증
		orderItemValidator.validate(command.getOrderItems());
		// 4. 구매 수량 제한 검증
		purchaseLimitValidator.validate(
			command.getUserId(),
			command.getTimeDealId(),
			command.getOrderItems(),
			timeDeal
		);
		// 5. 재고 예약 + OrderItem 생성
		List<OrderItem> orderItems = reserveStocksAndCreateOrderItems(command, timeDeal);
		// 6. 주문 엔티티 생성
		Order order = Order.create(
			command.getUserId(),
			orderItems,
			command.getPointUsed(),
			command.getShippingInfo(),
			command.getPaymentMethod()
		);
		// 7. 주문 예약 정보 추가
		for (CreateOrderCommand.OrderItemCommand itemCommand : command.getOrderItems()) {
			OrderReservation orderReservation = OrderReservation.create(
				UUID.fromString(itemCommand.getTimeDealStockId()),
				itemCommand.getQuantity()
			);
			order.addReservation(orderReservation);
		}
		// 8. 주문 저장
		Order savedOrder = orderRepository.save(order);
		// 9. 대기열 토큰 TTL 연장 (15분)
		queuePort.extendTokenTtl(command.getTimeDealId(), command.getUserId(), 900);

		return mapToResult(savedOrder);
	}

	/*
	* 재고 예약 + OrderItem 생성
	* 타임딜 서비스에서 상품 정보를 포함한 재고 상세 정보 조회
	* */
	private List<OrderItem> reserveStocksAndCreateOrderItems(
		CreateOrderCommand command,
		TimeDealInfo timeDeal
	) {
		List<OrderItem> orderItems = new ArrayList<>();

		for (CreateOrderCommand.OrderItemCommand itemCommand : command.getOrderItems()) {
			UUID timeDealStockId = UUID.fromString(itemCommand.getTimeDealStockId());
			String lockKey = "stock:" + timeDealStockId + ":lock";

			OrderItem orderItem = lockManager.executeWithLock(
				lockKey,
				5, // 5초 대기
				3, // 3초 TTL
				TimeUnit.SECONDS,
				() -> {
					// 1. 타임딜 재고 상세 정보 조회 (상품 정보 포함)
					TimeDealStockDetail stockDetail = timeDealStockPort
						.getTimeDealStockDetail(timeDealStockId);

					// 2. 상품 활성 상태 검증
					timeDealStockValidator.validate(stockDetail);

					// 3. 재고 예약 요청
					StockReservationResult result = timeDealStockPort.reserveStock(
							timeDealStockId,
							itemCommand.getQuantity(),
							command.getUserId()
						);
					if (!result.isSuccess()) {
						handleStockDepletion(
							command.getTimeDealId(),
							command.getUserId(),
							result.getAvailableStock()
						);
						throw new BusinessException(OrderErrorCode.STOCK_DEPLETED);
					}

					// 4. ProductSnapshot 생성 (타임딜 서비스에서 받은 정보로)
					ProductSnapshot snapshot = ProductSnapshot.builder()
						.timeDealStockId(timeDealStockId.toString())
						.productId(stockDetail.getProductId())
						.productName(stockDetail.getProductName())
						.productDescription(stockDetail.getProductDescription())
						.optionId(stockDetail.getOptionId())
						.optionName(stockDetail.getOptionName())
						.sellerId(stockDetail.getSellerId())
						.sellerName(stockDetail.getSellerName())
						.originalPrice(stockDetail.getProductPrice())
						.timeDealId(timeDeal.getTimeDealId())
						.timeDealTitle(timeDeal.getTitle())
						.discountRate(timeDeal.getDiscountRate())
						.category(stockDetail.getCategory())
						.build();

					// 5. OrderItem 생성
					return OrderItem.create(
						timeDealStockId,
						itemCommand.getQuantity(),
						stockDetail.getProductPrice(),
						timeDeal.getDiscountPrice(),
						snapshot
					);
				}
			);

			orderItems.add(orderItem);
		}

		return orderItems;
	}

	/* 재고 소진 처리 */
	private void handleStockDepletion(String timeDealId, Long userId, Integer availableStock) {
		orderEventPort.publishStockDepletedEvent(
			timeDealId,
			userId,
			availableStock,
			Instant.now()
		);
	}

	/* 주문 결과 매핑 */
	private CreateOrderResult mapToResult(Order order) {
		List<CreateOrderResult.OrderItemResult> itemResults = order.getOrderItems().stream()
			.map(item -> CreateOrderResult.OrderItemResult.builder()
				.orderItemId(item.getOrderItemId())
				.productName(item.getProductName())
				.optionName(item.getProductSnapshot().getOptionName())
				.quantity(item.getQuantity())
				.unitPrice(item.getUnitPrice())
				.discountPrice(item.getDiscountPrice())
				.subtotal(item.getSubtotal())
				.build())
			.collect(Collectors.toList());

		Instant reservationExpiresAt = order.getReservations().stream()
			.map(OrderReservation::getExpiresAt)
			.findFirst()
			.orElse(Instant.now().plus(15, ChronoUnit.MINUTES));

		return CreateOrderResult.builder()
			.orderId(order.getOrderId())
			.orderStatus(order.getStatus().name())
			.totalAmount(order.getTotalAmount())
			.pointUsed(order.getPointUsed())
			.finalAmount(order.getFinalAmount())
			.orderedAt(order.getOrderedAt())
			.reservationExpiresAt(reservationExpiresAt)
			.orderItems(itemResults)
			.build();
	}
}
