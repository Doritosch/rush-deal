package com.rushcrew.order_service.order.batch.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
* 자동 구매확정 Quartz Job - 매 시간 정각에 실행
* */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoConfirmScheduler extends QuartzJobBean {

	private final JobLauncher jobLauncher;
	private final Job autoConfirmPurchaseJob;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("====== 자동 구매확정 배치 작업 시작 ======");
		try {
			// Job parameters 생성 (매번 다른 파라미터로 실행되도록)
			JobParameters params = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

			// Job 실행
			jobLauncher.run(autoConfirmPurchaseJob, params);

			log.info("====== 자동 구매확정 배치 작업 완료 ======");

		} catch (Exception e) {
			log.error("====== 자동 구매확정 배치 작업 실패 ======");
		}
	}
}
