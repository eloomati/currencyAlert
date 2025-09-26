package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.config.CurrencyProperties;
import io.mhetko.datagatherer.model.CurrencyEntity;
import io.mhetko.datagatherer.model.ExchangeRateHistoryEntity;
import io.mhetko.datagatherer.model.RateEntity;
import io.mhetko.datagatherer.provider.OpenExchangeRatesClient;
import io.mhetko.datagatherer.repository.CurrencyRepository;
import io.mhetko.datagatherer.repository.ExchangeRateHistoryRepository;
import io.mhetko.datagatherer.repository.RateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentUpdateService {

    private final RateRepository rateRepository;
    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;
    private final CurrencyRepository currencyRepository;
    private final OpenExchangeRatesClient openExchangeRatesClient;


    @Transactional
    public void fetchAndSaveRates(String base, Iterable<String> symbols, OffsetDateTime asOf) {
        var rates = openExchangeRatesClient.latest(base, symbols);
        for (var entry : rates.entrySet()) {
            saveRate(base, entry.getKey(), entry.getValue(), asOf);
        }
    }

    private void saveRate(String base, String symbol, Double rate, OffsetDateTime asOf) {
        RateEntity rateEntity = buildOrUpdateRateEntity(base, symbol, rate, asOf);
        rateRepository.save(rateEntity);

        ExchangeRateHistoryEntity historyEntity = buildHistoryEntity(base, symbol, rate, asOf);
        exchangeRateHistoryRepository.save(historyEntity);
    }

    private RateEntity buildOrUpdateRateEntity(String base, String symbol, Double rate, OffsetDateTime asOf) {
        CurrencyEntity baseCurrency = currencyRepository.findByCode(base)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono waluty bazowej: " + base));
        CurrencyEntity targetCurrency = currencyRepository.findByCode(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono waluty docelowej: " + symbol));

        return rateRepository.findByBaseAndTarget(baseCurrency, targetCurrency)
                .orElse(RateEntity.builder()
                        .base(baseCurrency)
                        .target(targetCurrency)
                        .build())
                .toBuilder()
                .rate(rate)
                .asOf(asOf)
                .updatedAt(OffsetDateTime.now())
                .build();
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
