package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.application.result.TimeDealResult;
import com.rushcrew.timedeal.domain.entity.TimeDeal;
import com.rushcrew.timedeal.domain.entity.TimeDealProduct;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDealJpaRepository extends JpaRepository<TimeDeal, UUID> {

    @Query("""
                SELECT new com.rushcrew.timedeal.application.result.TimeDealResult(
                            td.id,
                            td.timeDealInfo.title,
                            td.timeDealInfo.description,
                            td.price.amount,
                            td.period.startAt,
                            td.period.endAt,
                            td.status
                       )
                FROM TimeDeal td
                WHERE td.status <> com.rushcrew.timedeal.domain.vo.TimeDealStatus.ENDED
                  AND (:status IS NULL OR td.status = :status)
                  AND td.deletedAt IS NULL
        """)
    Page<TimeDealResult> findNotEndedByStatus(
        @Param(value = "status") TimeDealStatus status,
        Pageable pageable
    );


    @Query("""
                   SELECT td
                    FROM TimeDeal td
                   WHERE td.id = :timeDealId
                    AND td.status <> :timeDealStatus
                    AND td.deletedAt IS NULL
        """)
    Optional<TimeDeal> findByIdAndStatusNot(
        @Param(value = "timeDealId") UUID timeDealId,
        @Param(value = "timeDealStatus") TimeDealStatus timeDealStatus
    );

    @Query("""
                  SELECT tdp
                  FROM TimeDealProduct tdp
                  WHERE tdp.deletedAt IS NULL
                    AND tdp.id = :productId
        """)
    Optional<TimeDealProduct> findProductByProductId(
        @Param(value = "productId") UUID productId
    );
}
