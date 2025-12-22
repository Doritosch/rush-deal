package com.rushcrew.order_service.application.saga.dto;

import java.util.UUID;

import com.rushcrew.order_service.domain.model.saga.SagaInstance;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SagaContext {

	private UUID sagaId;
	private Long userId;

	/** Saga 재개용 */
	public static SagaContext restore(SagaInstance saga) {
		return SagaContext.builder()
			.sagaId(saga.getSagaId())
			.userId(saga.getUserId())
			.build();
	}
}
