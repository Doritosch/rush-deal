package com.rushdeal.product.domain.vo;

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
            throw new IllegalArgumentException("가격은 0원 미만일 수 없습니다.");
        }
        this.amount = amount;
    }

    public static Price of(Long amount) {
        return new Price(amount);
    }
}
