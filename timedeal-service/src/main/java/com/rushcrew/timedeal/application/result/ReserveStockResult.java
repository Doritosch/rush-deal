package com.rushcrew.timedeal.application.result;

import java.math.BigDecimal;

public record ReserveStockResult(
	Boolean success,
	Integer availableStock,
	String message,
	BigDecimal discountPrice
) {

	public static ReserveStockResult of(
		Long available,
		String message,
		BigDecimal discountPrice
	) {
		return new ReserveStockResult(
			true,
			available.intValue(),
			message,
			discountPrice
		);
	}

	// 기존 호출부 호환용
	public static ReserveStockResult of(Long available, String message) {
		return new ReserveStockResult(
			true,
			available.intValue(),
			message,
			null
		);
	}
}

