package io.mhetko.datagatherer.repository;

import io.mhetko.datagatherer.model.ExchangeRateHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistoryEntity, UUID> {
    List<ExchangeRateHistoryEntity> findTop2ByBaseAndSymbolOrderByAsOfDesc(String base, String symbol);
}
