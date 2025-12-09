package com.rushcrew.queue.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.vo.TimePeriod;
import com.rushcrew.queue.domain.vo.TrafficSetting;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_queue_policy", schema = "queue_schema")
@SQLRestriction("deleted_at is NULL")
public class QueuePolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "policy_id", nullable = false, updatable = false)
    private UUID policyId;

    // 상품 ID
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "name", nullable = false)
    private String timeDealName;

    // 타임딜 실행 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QueuePolicyStatus status;

    // vo 적용 (운영시간)
    @Embedded
    private TimePeriod timePeriod;

    // vo 적용 (활성 허용 인원, 활성 체크 주기, ttl)
    @Embedded
    private TrafficSetting trafficSetting;

    public static QueuePolicy create(UUID productId, String timeDealName, QueuePolicyStatus status,
        TimePeriod timePeriod, TrafficSetting trafficSetting) {
        return QueuePolicy.builder()
            .productId(productId)
            .timeDealName(timeDealName)
            .status(status)
            .timePeriod(timePeriod)
            .trafficSetting(trafficSetting)
            .build();
    }

    /**
     * 타임딜 정보 업데이트
     */
    public void update(String timeDealName, QueuePolicyStatus status,
        TimePeriod timePeriod, TrafficSetting trafficSetting) {
         this.timeDealName = timeDealName;
         this.status = status;
         this.timePeriod = timePeriod;
         this.trafficSetting = trafficSetting;
    }


}
