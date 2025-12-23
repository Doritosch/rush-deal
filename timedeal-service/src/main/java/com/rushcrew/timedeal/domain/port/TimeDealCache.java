package com.rushcrew.timedeal.domain.port;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TimeDealCache {

    void syncScheduleTime(UUID timeDealId, Instant time, TimeDealQueueKey queueKey);

    List<String> getTimeDealIds(TimeDealQueueKey queueKey);

    void removeTimedOut(TimeDealQueueKey queueKey, List<String> updatedIds);
}
