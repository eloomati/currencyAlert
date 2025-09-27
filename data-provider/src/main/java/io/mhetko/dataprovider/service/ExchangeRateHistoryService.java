// data-provider/src/main/java/io/mhetko/dataprovider/service/ExchangeRateHistoryService.java
package io.mhetko.dataprovider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.repository.ExchangeRateHistoryRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateHistoryService {

    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;

    public void saveHistory(String base, String symbol, Double rate, OffsetDateTime asOf) {
        ExchangeRateHistoryEntity entity = buildHistoryEntity(base, symbol, rate, asOf);
        try {
            exchangeRateHistoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate history entry ignored: base={}, symbol={}, asOf={}", base, symbol, asOf);
        }
    }

    public List<ExchangeRateHistoryEntity> getLastTwo(String symbol) {
        return exchangeRateHistoryRepository
                .findTop2BySymbolOrderByAsOfDesc(symbol);
    }

    public Map<String, List<ExchangeRateHistoryEntity>> getHistoryByBaseGrouped(String base) {
        return exchangeRateHistoryRepository.findByBaseOrderByAsOfDesc(base.toUpperCase())
                .stream()
                .collect(Collectors.groupingBy(ExchangeRateHistoryEntity::getSymbol));
    }

    public List<ExchangeRateHistoryEntity> getLatestRatesByBase(String base) {
        return exchangeRateHistoryRepository.findLatestRatesByBase(base.toUpperCase());
    }

    private ExchangeRateHistoryEntity buildHistoryEntity(String base, String symbol, Double rate, OffsetDateTime asOf) {
        return ExchangeRateHistoryEntity.builder()
                .id(UUID.randomUUID())
                .base(base)
                .symbol(symbol)
                .rate(rate)
                .asOf(asOf)
                .ingestedAt(OffsetDateTime.now())
                .build();
    }
}