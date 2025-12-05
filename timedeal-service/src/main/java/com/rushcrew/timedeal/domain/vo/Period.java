package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import jakarta.persistence.Embeddable;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    private Timestamp startAt;
    private Timestamp endAt;

    private Period(Timestamp startAt, Timestamp endAt) {
        if (startAt == null || endAt == null || !isValidPeriod(startAt, endAt)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_PERIOD);
        }
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static Period of(Timestamp startAt, Timestamp endAt) {
        return new Period(startAt, endAt);
    }

    public boolean isValidPeriod(Timestamp startAt, Timestamp endAt) {
        return startAt.before(endAt);
    }
}
