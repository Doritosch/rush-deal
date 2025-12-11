package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDealJpaRepository extends JpaRepository<TimeDeal, UUID> {

}
