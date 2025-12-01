package com.rushcrew.product.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerId {

    private Long id;

    private SellerId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("유효하지 않는 판매자ID 입니다.");
        }
        this.id = id;
    }

    public static SellerId of(Long id) {
        return new SellerId(id);
    }
}
