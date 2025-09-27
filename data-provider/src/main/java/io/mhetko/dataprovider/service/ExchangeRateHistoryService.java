package io.mhetko.dataprovider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.repository.ExchangeRateHistoryRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ExchangeRateHistoryService {

    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;

    public void saveHistory(String base, String symbol, Double rate, OffsetDateTime asOf) {
        ExchangeRateHistoryEntity entity = buildHistoryEntity(base, symbol, rate, asOf);
        exchangeRateHistoryRepository.save(entity);
    }

    public List<ExchangeRateHistoryEntity> getLastTwo(String symbol) {
        return exchangeRateHistoryRepository
                .findTop2BySymbolOrderByAsOfDesc(symbol);
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
