package com.rushcrew.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.InvalidTimeDealException;
import com.rushcrew.order.application.port.dto.TimeDealInfo;
import com.rushcrew.order.application.port.dto.TimeDealStatus;

@Component
public class TimeDealValidator {

	public void validate(TimeDealInfo timeDeal) {
		if (timeDeal.getStatus() != TimeDealStatus.IN_PROGRESS) {
			throw new InvalidTimeDealException();
		}
	}
}
