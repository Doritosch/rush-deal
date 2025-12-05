package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealResponse {
	@NonNull private String timeDealId;
	@NonNull private String title;
	@NonNull private TimeDealResponseStatus status;
	@NonNull private BigDecimal discountPrice;
	@NonNull private BigDecimal discountRate;
	@NonNull private Integer limitQuantity;
}
