package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealStockDetailResponse {
	private UUID timeDealStockId;
	private String timeDealId;
	private String productId;
	private String optionId;
	private Integer availableStock;     // 주문 가능한 재고
	private Integer reservedStock;      // 예약된 재고
	private Integer soldStock;          // 판매 완료된 재고
	private TimeDealResponseStatus status;

	// 상품 정보 - 타임딜 서비스가 상품 서비스에서 조회해서 포함시켜줌
	private String productName;
	private String productDescription;
	private BigDecimal productPrice; // 원가
	private String category;
	private String optionName;
	private String sellerId;
	private String sellerName;
	private boolean isActive;
}
