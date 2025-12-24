package com.rushcrew.timedeal.domain.port;

import lombok.Getter;

@Getter
public enum TimeDealQueueKey {
    START("td:queue:start"),
    END("td:queue:end");

    private final String key;

    TimeDealQueueKey(String key) {
        this.key = key;
    }
}
