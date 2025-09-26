package io.mhetko.datagatherer.repository;

import io.mhetko.datagatherer.model.ExchangeRateHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistoryEntity, UUID> {
    // Możesz dodać własne metody wyszukiwania, np. po base, symbol, asOf
}
