package com.rushcrew.timedeal.domain.repository;

import com.rushcrew.timedeal.domain.entity.TimeDealStock;

public interface StockRepository {

    void save(TimeDealStock newStock);
}
