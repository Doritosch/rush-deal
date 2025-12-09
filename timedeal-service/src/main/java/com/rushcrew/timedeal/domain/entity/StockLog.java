package com.rushcrew.timedeal.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.Quantity;
import com.rushcrew.timedeal.domain.vo.Type;
import jakarta.persistence.AttributeOverride;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_stock_log", schema = "timedeal_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class StockLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_deal_stock_id", nullable = false)
    private TimeDealStock timeDealStock;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "order_id"))
    private OrderId orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Embedded
    @AttributeOverride(name = "count", column = @Column(name = "quantity", nullable = false))
    private Quantity quantity;

    @Column(columnDefinition = "TEXT")
    private String description;
}
