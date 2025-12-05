package com.rushcrew.timedeal.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.timedeal.domain.vo.LimitQuantity;
import com.rushcrew.timedeal.domain.vo.Period;
import com.rushcrew.timedeal.domain.vo.Price;
import com.rushcrew.timedeal.domain.vo.TimeDealInfo;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
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
@Table(name = "p_time_deal", schema = "timedeal_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class TimeDeal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "title", column = @Column(nullable = false)),
        @AttributeOverride(name = "description", column = @Column(nullable = false))
    })
    private TimeDealInfo timeDealInfo;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "discount_price", nullable = false))
    private Price price; // 할인 가격 (타임딜에서 판매하는 가격)

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "limit_quantity"))
    private LimitQuantity limitQuantity; // 한 계정당 제한되는 구매 수량

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startAt", column = @Column(name = "start_at", nullable = false)),
        @AttributeOverride(name = "endAt", column = @Column(name = "end_at", nullable = false))
    })
    private Period period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TimeDealStatus status = TimeDealStatus.SCHEDULED;

    @OneToMany(mappedBy = "timeDeal", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<TimeDealProduct> timeDealProducts = new ArrayList<>();

}

