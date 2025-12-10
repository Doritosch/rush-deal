package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import java.util.Optional;
import java.util.UUID;
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
}
