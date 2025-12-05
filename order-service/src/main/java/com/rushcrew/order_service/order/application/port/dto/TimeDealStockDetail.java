package com.rushcrew.order_service.order.application.port.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeDealStockDetail {
	// 타임딜 재고 정보
	private UUID timeDealStockId;
	private String timeDealId;

	private Integer availableStock;     // 주문 가능한 재고
	private Integer reservedStock;      // 예약된 재고
	private Integer soldStock;          // 판매 완료된 재고
	private TimeDealStatus status;

	// 상품 정보
	private String productId;
	private String productName;
	private String productDescription;
	private BigDecimal productPrice; // 원가
	private String category;

	// 옵션 정보
	private String optionId;
	private String optionName;

	// 판매자 정보
	private String sellerId;
	private String sellerName;

	private boolean isActive;
}
