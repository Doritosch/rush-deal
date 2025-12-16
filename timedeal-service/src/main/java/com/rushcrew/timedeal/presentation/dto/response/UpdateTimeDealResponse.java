package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.UpdateTimeDealResult;
import java.time.Instant;
import java.util.UUID;

public record UpdateTimeDealResponse(
    UUID timeDealId,
    String title,
    String description,
    Long price,
    Long limitQuantity,
    Instant startAt,
    Instant endAt
) {

    public static UpdateTimeDealResponse from(UpdateTimeDealResult result) {
        return new UpdateTimeDealResponse(
            result.timeDealId(),
            result.title(),
            result.description(),
            result.price(),
            result.limitQuantity(),
            result.startAt(),
            result.endAt()
        );
    }
}
