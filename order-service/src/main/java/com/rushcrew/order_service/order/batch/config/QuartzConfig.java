package com.rushcrew.order_service.order.batch.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rushcrew.order_service.order.batch.job.AutoConfirmScheduler;

@Configuration
public class QuartzConfig {

	/* 자동 구매확정 Job Detail */
	@Bean
	public JobDetail autoConfirmJobDeatail() {
		return JobBuilder.newJob(AutoConfirmScheduler.class)
			.withIdentity("autoConfirmPurchaseJob")
			.storeDurably()
			.build();
	}

	/* 자동 구매확정 Trigger - 매 시간 정각에 실행 */
	@Bean
	public Trigger autoConfirmTrigger() {
		return TriggerBuilder.newTrigger()
			.forJob(autoConfirmJobDeatail())
			.withIdentity("autoConfirmPurchaseTrigger")
			.withSchedule(
				// 테스트: 1분마다 실행
				CronScheduleBuilder.cronSchedule("0 * * * * ?")

				// // 운영: 매 시간 정각 실행
				// CronScheduleBuilder.cronSchedule("0 0 * * * ?")
			).build();
	}
}
