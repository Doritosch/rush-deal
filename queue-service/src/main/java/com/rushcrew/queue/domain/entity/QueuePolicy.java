package com.rushcrew.queue.domain.entity;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import com.rushcrew.queue.domain.enums.QueueStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // 활성 허용 인원
    @Column(name = "limit_size", nullable = false)
    private Integer limitSize;

    // 활성 체크 주기 (몇 초 마다 확인해서 들여보낼 지 주기)
    @Column(name = "queue_gap", nullable = false)
    private Integer queueGap;

    // 토큰 유효 시 (Active 토큰의 만료 시간 (예: 300초))
    @Column(name = "ttl", nullable = false)
    private Integer ttl;

    public static QueuePolicy create(UUID productId, String timeDealName, QueueStatus status,
        LocalDateTime startTime, LocalDateTime endTime, Integer limitSize, Integer queueGap,
        Integer ttl) {
        return QueuePolicy.builder()
            .productId(productId)
            .timeDealName(timeDealName)
            .status(status)
            .startTime(startTime)
            .endTime(endTime)
            .limitSize(limitSize)
            .queueGap(queueGap)
            .ttl(ttl)
            .build();
    }
}
