package com.rushcrew.order.infrastructure.adapter.out.client;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.port.dto.PointInfo;
import com.rushcrew.order.application.port.out.UserPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {
	@Override
	public PointInfo getPointBalance(Long userId) {
		return null;
	}
}
