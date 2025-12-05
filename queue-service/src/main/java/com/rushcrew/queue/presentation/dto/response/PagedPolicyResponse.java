package com.rushcrew.queue.presentation.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PagedPolicyResponse(
    List<QueuePolicyResponse> policies,
    int totalPages,
    long totalElements,
    int currentPage,
    boolean isFirst,
    boolean isLast
) {
    public static PagedPolicyResponse of(List<QueuePolicyResponse> policies, int totalPages,
        long totalElements, int currentPage, boolean isFirst, boolean isLast) {
        return new PagedPolicyResponse(
            policies,
            totalPages,
            totalElements,
            currentPage,
            isFirst,
            isLast
        );
    }
}
