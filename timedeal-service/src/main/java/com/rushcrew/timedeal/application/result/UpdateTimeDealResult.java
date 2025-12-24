package com.rushcrew.timedeal.application.result;

import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.time.Instant;
import java.util.UUID;

public record UpdateTimeDealResult(
    UUID timeDealId,
    String title,
    String description,
    Long price,
    Long limitQuantity,
    Instant startAt,
    Instant endAt
) {

    public static UpdateTimeDealResult from(TimeDeal timeDeal) {
        return new UpdateTimeDealResult(
            timeDeal.getId(),
            timeDeal.getTimeDealInfo().getTitle(),
            timeDeal.getTimeDealInfo().getDescription(),
            timeDeal.getPrice().getAmount(),
            timeDeal.getLimitQuantity() == null ? null : timeDeal.getLimitQuantity().getQuantity(),
            timeDeal.getPeriod().getStartAt(),
            timeDeal.getPeriod().getEndAt()
        );
    }
}

