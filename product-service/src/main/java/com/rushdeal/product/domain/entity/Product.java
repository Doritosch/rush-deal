package com.rushdeal.product.domain.entity;

import com.rushdeal.product.domain.vo.Category;
import com.rushdeal.product.domain.vo.Price;
import com.rushdeal.product.domain.vo.ProductInfo;
import com.rushdeal.product.domain.vo.SellerId;
import com.rushdeal.product.presentation.dto.request.CreateProductRequest;
import com.rushdeal.product.presentation.dto.request.UpdateProductRequest;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_product", schema = "product_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "seller_id", nullable = false))
    private SellerId userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "product_name", nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "description", nullable = false))
    })
    private ProductInfo productInfo;

    @Embedded
    @AttributeOverride(name = "price", column = @Column(nullable = false))
    private Price price;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    public static Product create(CreateProductRequest request) {
        return Product.builder()
            .userId(SellerId.of(request.userId()))
            .productInfo(ProductInfo.of(request.productName(), request.description()))
            .price(Price.of(request.price()))
            .category(request.category())
            .build();
    }

    public void addOption(ProductOption option) {
        this.options.add(option);
    }

    public void update(UpdateProductRequest request) {
        if (request.companyName() != null) {
            this.companyName = request.companyName();
        }
        if (request.category() != null) {
            this.category = request.category();
        }
        ProductInfo.of(request.productName(), request.description());
        Price.of(request.price());
    }
}
