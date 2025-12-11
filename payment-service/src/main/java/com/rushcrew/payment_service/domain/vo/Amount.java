package com.rushcrew.payment_service.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Amount {

    private Long total;
    private Long taxFree;
    private Long vat;
    private Long supply;
    private Long discount;
    private Long paid;

    public Amount(Long total,
                  Long taxFree,
                  Long vat,
                  Long supply,
                  Long discount,
                  Long paid) {
        this.total = total;
        this.taxFree = taxFree;
        this.vat = vat;
        this.supply = supply;
        this.discount = discount;
        this.paid = paid;
    }
}
