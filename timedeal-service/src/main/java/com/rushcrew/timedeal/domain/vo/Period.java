package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    private static final long MINIMUM_START_DELAY_SECONDS = 5;

    private Instant startAt;
    private Instant endAt;

    private Period(Instant startAt, Instant endAt) {
        if (startAt == null || endAt == null || !isValidPeriod(startAt, endAt)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_PERIOD);
        }
        validateMinDelay(startAt);

        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static Period of(Instant startAt, Instant endAt) {
        return new Period(startAt, endAt);
    }

    public boolean isValidPeriod(Instant startAt, Instant endAt) {
        return startAt.isBefore(endAt);
    }

    private void validateMinDelay(Instant start) {
        Instant minAllowedStart = Instant.now().plusSeconds(MINIMUM_START_DELAY_SECONDS);

        if (start.isBefore(minAllowedStart)) {
            throw new BusinessException(TimeDealErrorCode.START_TIME_TOO_CLOSE);
        }
    }
}
