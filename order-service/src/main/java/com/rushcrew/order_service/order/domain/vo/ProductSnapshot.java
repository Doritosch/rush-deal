package com.rushcrew.order_service.order.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

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
	private String productId;
	private String productName;
	private String productDescription;
	private String optionId;
	private String optionName;
	private String sellerId;
	private String sellerName;
	private BigDecimal originalPrice;
	private String timeDealId;
	private String timeDealTitle;
	private BigDecimal discountRate;
	private String category;

}
