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
public class ProductInfo {

    private String name;
    private String description;

    private ProductInfo(String name, String description) {
        validateName(name);
        validateDescription(description);

        this.name = name;
        this.description = description;
    }

    public static ProductInfo of(String name, String description) {
        return new ProductInfo(name, description);
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
    }

    private void validateDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("상품 설명은 필수입니다.");
        }
    }
}
