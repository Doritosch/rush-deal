package com.rushcrew.order.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShippingInfo {

	@Column(nullable = false, length = 50)
	private String recipientName;

	@Column(nullable = false, length = 20)
	private String recipientPhone;

	@Column(nullable = false, length = 10)
	private String zipCode;

	@Column(nullable = false, length = 255)
	private String addressBase;

	@Column(nullable = false, length = 255)
	private String addressDetail;

	@Column(length = 100)
	private String deliveryMessage;

}
