package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistoryEntity, UUID> {
    List<ExchangeRateHistoryEntity> findTop2BySymbolOrderByAsOfDesc(String symbol);

    @Query("""
    SELECT e FROM ExchangeRateHistoryEntity e
    WHERE e.base = :base AND e.asOf = (
        SELECT MAX(e2.asOf) FROM ExchangeRateHistoryEntity e2
        WHERE e2.base = :base AND e2.symbol = e.symbol
    )
""")
    List<ExchangeRateHistoryEntity> findLatestRatesByBase(String base);

    List<ExchangeRateHistoryEntity> findByBaseOrderByAsOfDesc(String symbol);
}
