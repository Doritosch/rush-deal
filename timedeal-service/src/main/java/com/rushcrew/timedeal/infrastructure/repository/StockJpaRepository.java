package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.application.result.StockResult;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockJpaRepository extends JpaRepository<TimeDealStock, UUID> {

    @Query("""
                  SELECT tds
                  FROM TimeDealStock tds
                  WHERE tds.id = :stockId
                    AND tds.deletedAt IS NULL
        """)
    Optional<TimeDealStock> findNotDeletedById(
        @Param("stockId") UUID stockId
    );

    @Query("""
                  SELECT new com.rushcrew.timedeal.application.result.StockResult(
                                  tds.id,
                                  tdp.id,
                                  tds.stockCounts.available,
                                  tds.stockCounts.reserved,
                                  tds.stockCounts.sold,
                                  tdp.status,
                                  tds.updatedAt
                             )
                  FROM TimeDealStock tds
                  JOIN tds.timeDealProduct tdp
                  JOIN tdp.timeDeal td
                  WHERE (:keyword IS NULL OR CAST(td.timeDealInfo.title AS STRING) LIKE :keyword)
                    AND (:productId IS NULL OR tdp.id = :productId)
                    AND (:status IS NULL OR tds.status = :status)
                    AND tds.deletedAt IS NULL
        """)
    Page<StockResult> findStockResults(
        @Param("keyword") String keyword,
        @Param("productId") UUID productId,
        @Param("status") TimeDealStockStatus status,
        Pageable pageable
    );
}
