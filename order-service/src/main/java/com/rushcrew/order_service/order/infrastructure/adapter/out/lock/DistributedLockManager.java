package com.rushcrew.order_service.order.infrastructure.adapter.out.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockManager {

	private final RedissonClient redissonClient;

	/**
	 * 분산 락을 획득하고 비즈니스 로직 실행
	 *
	 * @param lockKey 락 키
	 * @param waitTime 락 획득 대기 시간
	 * @param leaseTime 락 보유 시간
	 * @param timeUnit 시간 단위
	 * @param supplier 실행할 비즈니스 로직
	 * @return 비즈니스 로직 실행 결과
	 */
	public <T> T executeWithLock(
		String lockKey,
		long waitTime,
		long leaseTime,
		TimeUnit timeUnit,
		Supplier<T> supplier
	) {
		RLock lock = redissonClient.getLock(lockKey);

		try {
			boolean acquired = lock.tryLock(waitTime, leaseTime, timeUnit);

			if (!acquired) {
				log.warn("Failed to acquire lock: {}", lockKey);
				throw new LockAcquisitionException("락 획득에 실패했습니다: " + lockKey);
			}

			log.debug("Lock acquired: {}", lockKey);
			return supplier.get();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while acquiring lock: {}", lockKey, e);
			throw new LockAcquisitionException("락 획득 중 인터럽트 발생", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
				log.debug("Lock released: {}", lockKey);
			}
		}
	}

	public static class LockAcquisitionException extends RuntimeException {
		public LockAcquisitionException(String message) {
			super(message);
		}

		public LockAcquisitionException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
