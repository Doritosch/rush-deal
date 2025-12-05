package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealStockDetailResponse {
	@NonNull private String timeDealStockId;
	@NonNull private String timeDealId;
	@NonNull private String productId;
	@NonNull private String optionId;
	@NonNull private Integer availableStock;     // 주문 가능한 재고
	@NonNull private Integer reservedStock;      // 예약된 재고
	@NonNull private Integer soldStock;          // 판매 완료된 재고
	@NonNull private TimeDealResponseStatus status;

	// 상품 정보 - 타임딜 서비스가 상품 서비스에서 조회해서 포함시켜줌
	@NonNull private String productName;
	@NonNull private String productDescription;
	@NonNull private BigDecimal productPrice; // 원가
	@NonNull private String category;
	@NonNull private String optionName;
	@NonNull private String sellerId;
	@NonNull private String sellerName;
	@NonNull private boolean isActive;
}
