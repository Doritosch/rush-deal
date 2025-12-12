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
public class Card {

    private String publisher;
    private String issuer;
    private String brand;
    private String type;
    private String ownerType;
    private String bin;
    private String name;
    private String number;

    public Card(
            String publisher,
            String issuer,
            String brand,
            String type,
            String ownerType,
            String bin,
            String name,
            String number
    ) {
        this.publisher = publisher;
        this.issuer = issuer;
        this.brand = brand;
        this.type = type;
        this.ownerType = ownerType;
        this.bin = bin;
        this.name = name;
        this.number = number;
    }
}
