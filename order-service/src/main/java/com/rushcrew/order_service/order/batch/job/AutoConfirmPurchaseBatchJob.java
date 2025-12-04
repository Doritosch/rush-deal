package com.rushcrew.order_service.order.batch.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.rushcrew.order_service.order.application.port.out.PointEventPort;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.enums.OrderStatus;
import com.rushcrew.order_service.order.infrastructure.adapter.out.persistence.OrderJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AutoConfirmPurchaseBatchJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final OrderJpaRepository orderJpaRepository;
	private final PointEventPort pointEventPort;

	/* 자동 구매확정 Job */
	@Bean
	public Job autoConfirmPurchaseJob() {
		return new JobBuilder("autoConfirmPurchaseJob", jobRepository)
			.start(autoConfirmPurchaseStep())
			.build();
	}

	/* 자동 구매확정 Step */
	@Bean
	public Step autoConfirmPurchaseStep() {
		return new StepBuilder("autoConfirmPurchaseStep", jobRepository)
			.<Order, Order>chunk(100, transactionManager)
			.reader(autoConfirmTargetOrderReader())
			.processor(autoConfirmProcessor())
			.writer(autoConfirmWriter())
			.build();
	}

	/*
	 * Reader: 자동 구매확정 대상 주문 조회
	 * - 상태: PAID
	 * - auto_confirm_scheduled_at <= 현재 시간
	 */
	@Bean
	public RepositoryItemReader<Order> autoConfirmTargetOrderReader() {
		return new RepositoryItemReaderBuilder<Order>()
			.name("autoConfirmTargetOrderReader")
			.repository(orderJpaRepository)
			.methodName("findAllByStatusAndAutoConfirmScheduledAtBefore")
			.arguments(OrderStatus.PAID, Instant.now())
			.pageSize(100)
			.sorts(Map.of("orderedAt", Sort.Direction.ASC))
			.build();
	}

	/* Processor: 구매확정 처리 */
	@Bean
	public ItemProcessor<Order, Order> autoConfirmProcessor() {
		return order -> {
			log.info("자동 구매확정 처리중: orderId={}, userId={}",
				order.getOrderId(), order.getUserId());

			// 구매확정 처리 (Domain 메서드)
			order.confirmPurchase();

			return order;
		};
	}

	/* Writer: DB 저장 및 포인트 적립 이벤트 발행 */
	@Bean
	public ItemWriter<Order> autoConfirmWriter() {
		return orders -> {
			// 1. 주문 저장
			orderJpaRepository.saveAll(orders);

			// 2. 각 주문에 대해 포인트 적립 요청
			for (Order order : orders) {
				// 포인트 적립 금액 계산 (결제 금액의 5%) TODO: 포인트 적립 관련 적립 금액 넘겨주는지 결제한 금액을 넘겨주면 포인트에서 %계산해서 적립하는지
				BigDecimal earnAmount = order.getFinalAmount()
					.multiply(new BigDecimal("0.05"))
					.setScale(0, RoundingMode.DOWN);

				// 포인트 적립 요청 이벤트 발행
				pointEventPort.publishPointEarnRequested(
					order.getUserId(),
					order.getOrderId().toString(),
					earnAmount,
					"자동 구매확정",
					Instant.now()
				);

				log.info("자동 구매 확정 + 포인트 적립 요청: " +
						"orderId={}, userId={}, earnAmount={}",
					order.getOrderId(), order.getUserId(), earnAmount);
			}

			log.info("Auto confirmed {} orders", orders.size());
		};
	}
}
