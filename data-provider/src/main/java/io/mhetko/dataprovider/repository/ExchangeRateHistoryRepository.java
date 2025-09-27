package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistoryEntity, UUID> {
    List<ExchangeRateHistoryEntity> findTop2ByBaseAndSymbolOrderByAsOfDesc(String base, String symbol);

    Optional<ExchangeRateHistoryEntity> findTop1ByBaseAndSymbolOrderByAsOfDesc(String base, String symbol);
}
