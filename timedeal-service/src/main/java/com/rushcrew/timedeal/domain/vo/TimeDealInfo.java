package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeDealInfo {

    private String title;
    private String description;

    private TimeDealInfo(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_TITLE);
        }
        if (description == null || description.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_DESCRIPTION);
        }

        this.title = title;
        this.description = description;
    }

    public static TimeDealInfo of(String title, String description) {
        return new TimeDealInfo(title, description);
    }
}
