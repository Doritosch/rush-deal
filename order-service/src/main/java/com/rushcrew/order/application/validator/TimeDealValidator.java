package com.rushcrew.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.InvalidTimeDealException;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealStatus;

@Component
public class TimeDealValidator {

	public void validate(TimeDealResponse timeDeal) {
		if (timeDeal.getStatus() != TimeDealStatus.IN_PROGRESS) {
			throw new InvalidTimeDealException();
		}
	}
}
