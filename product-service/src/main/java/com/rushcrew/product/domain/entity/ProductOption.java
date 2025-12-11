package com.rushcrew.product.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.product.domain.model.UpdateOptionParams;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_product_option", schema = "product_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    public static ProductOption of(Product product, String size, String color) {
        return ProductOption.builder()
            .product(product)
            .size(size)
            .color(color)
            .build();
    }

    public void update(UpdateOptionParams params) {
        if (params.size() != null) {
            this.size = params.size();
        }
        if (params.color() != null) {
            this.color = params.color();
        }
    }
}
