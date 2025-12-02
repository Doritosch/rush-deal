package com.rushcrew.product.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.product.application.command.CreateProductCommand;
import com.rushcrew.product.application.command.UpdateProductCommand;
import com.rushcrew.product.domain.vo.Category;
import com.rushcrew.product.domain.vo.Price;
import com.rushcrew.product.domain.vo.ProductInfo;
import com.rushcrew.product.domain.vo.SellerId;
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
public class Product extends BaseEntity {

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
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false))
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

    public static Product create(CreateProductCommand command) {
        return Product.builder()
            .userId(command.sellerId())
            .companyName(command.companyName())
            .productInfo(command.productInfo())
            .price(command.price())
            .category(command.category())
            .build();
    }

    public void addOption(String size, String color) {
        this.options.add(ProductOption.of(this, size, color));
    }

    public void update(UpdateProductCommand command) {
        if (command.companyName() != null) this.companyName = command.companyName();
        if (command.category() != null) this.category = command.category();
        updateProductInfo(command.productName(), command.description());
        this.price = command.price() != null ? Price.of(command.price()) : this.price;
    }

    private void updateProductInfo(String newName, String newDescription) {
        String updatedName = newName != null ? newName : this.productInfo.getName();
        String updatedDescription =
            newDescription != null ? newDescription : this.productInfo.getDescription();

        this.productInfo = ProductInfo.of(updatedName, updatedDescription);
    }
}
