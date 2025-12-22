package com.rushcrew.order_service.application.saga.dto;

import lombok.*;

@Getter
public class SagaStepResult {

	private final boolean success;
	private final String errorMessage;

	private SagaStepResult(boolean success, String errorMessage) {
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public static SagaStepResult success() {
		return new SagaStepResult(true, null);
	}

	public static SagaStepResult failure(String errorMessage) {
		return new SagaStepResult(false, errorMessage);
	}

	public boolean isFailure() {
		return !success;
	}
}
