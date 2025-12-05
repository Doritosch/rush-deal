package com.rushcrew.auth_service.auth.domain.policy;

import java.util.List;

public class ConcurrentLoginPolicy {

    private final int maxConcurrentSessions;

    public ConcurrentLoginPolicy(int maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
    }

    public List<String> getTokensToRevoke(List<String> existingTokens) {
        if (existingTokens.size() <= maxConcurrentSessions) {
            return List.of();
        }

        int removeCount = existingTokens.size() - maxConcurrentSessions;
        return existingTokens.subList(0, removeCount);
    }
}
