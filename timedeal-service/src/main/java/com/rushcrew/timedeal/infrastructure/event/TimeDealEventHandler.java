package com.rushcrew.timedeal.infrastructure.event;

import com.rushcrew.timedeal.application.event.TimeDealScheduledEvent;
import com.rushcrew.timedeal.domain.port.TimeDealCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealEventHandler {

    private final TimeDealCache timeDealCache;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTimeDealScheduled(TimeDealScheduledEvent event) {
        try {
            timeDealCache.syncScheduleTime(event.timeDealId(), event.time(), event.queueKey());
        } catch (Exception e) {
            log.error(
                "TimeDealScheduledEvent 처리 중 Redis 반영 실패. timeDealId={}, time={}, queueKey={}",
                event.timeDealId(), event.time(), event.queueKey()
            );
        }
    }
}
