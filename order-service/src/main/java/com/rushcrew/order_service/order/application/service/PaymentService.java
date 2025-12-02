package com.rushcrew.order_service.order.application.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.order.application.command.RequestPaymentCommand;
import com.rushcrew.order_service.order.application.command.RequestPaymentResult;
import com.rushcrew.order_service.order.application.exception.OrderNotFoundException;
import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentEventPort paymentEventPort;

	public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
		// 1. 주문 조회
		Order order = orderRepository.findById(command.getOrderId())
			.orElseThrow(() -> new OrderNotFoundException());
		// 2. 본인 주문인지

		// 3. 주문 상태 PENDING 인지
		// 4. 예약 재고 만료되지 않았는지
		// 5. Saga ID 생성
		// 6. 포인트 차감 요청 이벤트 발행
		// 7. 포인트 사용 없으면 바로 결제 요청
	}

}
