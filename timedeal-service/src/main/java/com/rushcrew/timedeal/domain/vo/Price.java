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
public class Price {

    private Long amount;

    private Price(Long amount) {
        if (amount < 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_PRICE_RANGE);
        }

        this.amount = amount;
    }

    public static Price of(Long amount) {
        return new Price(amount);
    }

    /**
     * 타임딜 가격이 기본 가격보다 비싼지 비교
     *
     * @param originPrice 상품 기본 가격
     * @return this.amount가 더 크거나 같으면 true
     */
    public boolean isMoreExpensiveThan(Price originPrice) {
        return this.amount >= originPrice.amount;
    }
}
