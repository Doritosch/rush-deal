package com.rushcrew.order_service.order.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductSnapshot implements Serializable {

	private String timeDealStockId;
	private UUID productId;
	private String productName;
	private String productDescription;
	private UUID optionId;
	private String optionName;
	private UUID sellerId;
	private String sellerName;
	private BigDecimal originalPrice;
	private UUID timeDealId;
	private String timeDealTitle;
	private BigDecimal discountRate;
	private String category;

}
