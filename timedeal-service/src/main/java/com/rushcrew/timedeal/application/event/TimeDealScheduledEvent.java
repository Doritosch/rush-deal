package com.rushcrew.timedeal.application.event;

import com.rushcrew.timedeal.domain.port.TimeDealQueueKey;
import java.time.Instant;
import java.util.UUID;

public record TimeDealScheduledEvent(
    UUID timeDealId,
    Instant time,
    TimeDealQueueKey queueKey
) {

}
