package com.rushcrew.timedeal.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.domain.vo.ProductItemIds;
import com.rushcrew.timedeal.domain.vo.StockCounts;
import com.rushcrew.timedeal.domain.vo.TimeDealProductStatus;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_time_deal_stock", schema = "timedeal_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class TimeDealStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_deal_product_id", nullable = false, unique = true)
    private TimeDealProduct timeDealProduct;

    @Column(name = "time_deal_id", nullable = false)
    private UUID timeDealId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "productId", column = @Column(name = "product_id", nullable = false)),
        @AttributeOverride(name = "optionId", column = @Column(name = "option_id", nullable = false))
    })
    private ProductItemIds itemIds;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "available", column = @Column(name = "available_stock", nullable = false)),
        @AttributeOverride(name = "reserved", column = @Column(name = "reserved_stock", nullable = false)),
        @AttributeOverride(name = "sold", column = @Column(name = "sold_stock", nullable = false)),
    })
    private StockCounts stockCounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeDealStockStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "timeDealStock", fetch = FetchType.LAZY)
    @Builder.Default
    private List<StockLog> stockLogs = new ArrayList<>();

    public static TimeDealStock create(CreateStockCommand command,
        TimeDealProduct timeDealProduct) {
        TimeDealStock stock = TimeDealStock.builder()
            .timeDealProduct(timeDealProduct)
            .timeDealId(timeDealProduct.getTimeDeal().getId())
            .itemIds(
                ProductItemIds.of(command.productId(), timeDealProduct.getItemIds().getOptionId()))
            .stockCounts(StockCounts.init(command.totalStock()))
            .status(TimeDealStockStatus.AVAILABLE)
            .build();

        timeDealProduct.updateStatus(TimeDealProductStatus.IN_STOCK);

        StockLog log = StockLog.init(stock, command.totalStock());
        stock.stockLogs.add(log);

        return stock;
    }
}
