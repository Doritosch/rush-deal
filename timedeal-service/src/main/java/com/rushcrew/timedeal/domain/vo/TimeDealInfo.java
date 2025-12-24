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
    private Long sellerId;

    private TimeDealInfo(String title, String description, Long sellerId) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_TITLE);
        }
        if (description == null || description.isBlank()) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_DESCRIPTION);
        }

        if (sellerId == null) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_SELLER_ID);
        }

        this.title = title;
        this.description = description;
        this.sellerId = sellerId;
    }

    public static TimeDealInfo of(String title, String description, Long sellerId) {
        return new TimeDealInfo(title, description, sellerId);
    }
}
